package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.NotTowerDefense
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector

data class Game(
  val waitingRoom: Zone,
  val gameZone: Zone,
  val players: List<Player>,
  val spawnRoom: Zone,
  val endRoom: Zone,
  val pathStepZones: List<Zone>,
  val waves: List<List<GameWaveStep>>,
  val waveChangeTimeout: Int = 100,
  val towerOnBlockType: Material = Material.GOLD_BLOCK,
){
  private var lastSpawnTicksElapsed: Long = 0
  private var lastWaveEndTicksElapsed: Long = 0
  private var currentWaveIndex: Int = 0
  val mobs = mutableListOf<GameEntity>()
  val towers = mutableListOf<GameTower>()
  lateinit var loop: BukkitTask

  fun broadcast(message: String) {
    Bukkit.getOnlinePlayers().forEach { player -> player.sendMessage(message) }
  }

  private val isCurrentWaveEnded: Boolean
    get() = waves.getOrNull(currentWaveIndex)?.all { it.allSpawned } == true && mobs.isEmpty()

  private val isGameEnded: Boolean
    get() = isCurrentWaveEnded && currentWaveIndex + 1 >= waves.size

  private val currentWave: List<GameWaveStep>
    get() = waves[currentWaveIndex]

  private fun summonNextMob(){
    if(isCurrentWaveEnded) {
      lastWaveEndTicksElapsed++;
      if(lastWaveEndTicksElapsed > waveChangeTimeout) {
        currentWaveIndex++
        lastWaveEndTicksElapsed = 0
      }
      return
    }
    for (step in currentWave) {
      if(!step.timeoutPassed){
        if (step.timeout < lastSpawnTicksElapsed) step.timeoutPassed = true else break
      }
      if(step.delay < lastSpawnTicksElapsed) {
        mobs += step.spawnNextMob(spawnRoom, pathStepZones[0], Bukkit.getWorld("world")!!) ?: continue
        lastSpawnTicksElapsed = 0
        break
      }
    }
    lastSpawnTicksElapsed++
  }

  private fun moveMobs(){
    for (mob in mobs) {
      val aimedZone = if(mob.step < pathStepZones.size) pathStepZones[mob.step] else endRoom
      mob.move(aimedZone, mob.step >= pathStepZones.size)
    }
  }

  fun spawnOrGetTower(position: Position): GameTower {
    towers.removeIf { tower -> tower.entity.isDead || !tower.entity.isValid }
    var tower = towers.find { it.position == position }
    if(tower != null) return tower
    tower = GameTower(position, Bukkit.getWorld("world")!!, 0, GameTowerData("Tower", EntityType.SKELETON, 10, 5.0, 10, 10, 0, listOf(10), listOf()))
    towers.add(tower)
    return tower
  }

  fun shotMobs(){
    for (tower in towers) {
      tower.shotNearestEntity(mobs.map { it.entity })
    }
  }

  fun run() : BukkitTask {
    if(!::loop.isInitialized || loop.isCancelled) {
      broadcast("starting internal game loop")
      Bukkit.getWorld("world")?.entities?.filter { it !is Player }?.filter { gameZone.contains(Position.fromLocation(it.location)) }?.forEach { it.remove() }
      loop = Bukkit.getScheduler().runTaskTimer(NotTowerDefense.instance, {
        mobs.removeIf() { mob -> mob.entity.isDead || !mob.entity.isValid }
        if(isGameEnded) {
          broadcast("Game finished")
          stop()
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
    Bukkit.getScheduler().cancelTask(loop.taskId)
    loop.cancel()
    mobs.forEach { mob -> mob.entity.remove() }
    mobs.clear()
    towers.forEach { tower -> tower.entity.remove() }
    towers.clear()
  }
}