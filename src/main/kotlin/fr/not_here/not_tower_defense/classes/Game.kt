package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.models.GameConfig
import fr.not_here.not_tower_defense.managers.GameManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

data class Game(
  val gameConfig: GameConfig,
  val players: List<Player>,
  val towerOnBlockType: Material = Material.GOLD_BLOCK,
  val world: World = Bukkit.getWorld("world")!!,
){
  private var lastWaveEndTicksElapsed: Long = 0
  private var currentWaveIndex: Int = 0
  private val waves: List<GameWave> = gameConfig.waves.map { GameWave(it) }
  private val waveEntities = mutableListOf<GameWaveEntity>()
  private val spawnedTowers = mutableListOf<GameTower>()
  lateinit var loop: BukkitTask

  fun broadcast(message: String) {
    Bukkit.getOnlinePlayers().forEach { player -> player.sendMessage(message) }
  }

  fun hasEntityInTowers(entity: Entity) = spawnedTowers.any { it.entity == entity }
  fun hasEntityInWaves(entity: Entity) = waveEntities.any { it.entity == entity }
  fun getTowerFromEntity(entity: Entity) = spawnedTowers.find { it.entity == entity }
  fun getWaveEntityFromEntity(entity: Entity) = waveEntities.find { it.entity == entity }

  private val waveChangeTimeout get() = gameConfig.waveChangeTimeout
  private val isCurrentWaveEnded get() = waves.getOrNull(currentWaveIndex)?.allSpawned == true && waveEntities.isEmpty()
  private val isGameEnded get() = isCurrentWaveEnded && currentWaveIndex + 1 >= waves.size
  private val currentWave get() = waves[currentWaveIndex]

  private fun summonNextMob(){
    if(isCurrentWaveEnded) {
      lastWaveEndTicksElapsed++
      if(lastWaveEndTicksElapsed > waveChangeTimeout) {
        currentWaveIndex++
        lastWaveEndTicksElapsed = 0
      }
      return
    }
    waveEntities.addAll(currentWave.spawnNextMob(this, gameConfig.startRoom, gameConfig.pathSteps[0]))
  }

  private fun moveMobs(){
    for (mob in waveEntities) {
      val aimedZone = if(mob.step < gameConfig.pathSteps.size) gameConfig.pathSteps[mob.step] else gameConfig.endRoom
      mob.move(aimedZone, mob.step >= gameConfig.pathSteps.size)
    }
  }

  fun spawnOrGetTower(position: Position): GameTower {
    spawnedTowers.removeIf { tower -> tower.entity.isDead || !tower.entity.isValid }
    var tower = spawnedTowers.find { it.position == position }
    if(tower != null) return tower
    tower = GameTower(gameConfig.towers[0], position, world, 0)
    spawnedTowers.add(tower)
    return tower
  }

  fun shotMobs(){
    for (tower in spawnedTowers) {
      tower.shotNearestEntity(waveEntities.map { it.entity })
    }
  }

  fun run() : BukkitTask {
    players.forEach { player -> player.teleport(gameConfig.gameRoomSpawn!!.toLocation(world)) }
    if(!::loop.isInitialized || loop.isCancelled) {
      broadcast("starting internal game loop")
      world.entities?.filter { it !is Player }?.filter { gameConfig.gameRoom.contains(Position.fromLocation(it.location)) }?.forEach { it.remove() }
      loop = Bukkit.getScheduler().runTaskTimer(NotTowerDefense.instance, {
        waveEntities.removeIf() { mob -> mob.entity.isDead || !mob.entity.isValid }
        if(isGameEnded) {
          broadcast("Game finished")
          stop()
          GameManager.games.remove(this)
          return@runTaskTimer
        }
        summonNextMob()
        moveMobs()
        shotMobs()
      }, 0, 1)
    }
    return loop
  }

  fun stop() {
    waveEntities.forEach { mob -> mob.entity.remove() }
    waveEntities.clear()
    spawnedTowers.forEach { tower -> tower.entity.remove() }
    spawnedTowers.clear()
    Bukkit.getScheduler().cancelTask(loop.taskId)
    if(!::loop.isInitialized && !loop.isCancelled) loop.cancel()
  }
}