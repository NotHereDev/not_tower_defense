package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.models.GameConfig
import fr.not_here.not_tower_defense.config.models.GameTowerConfig
import fr.not_here.not_tower_defense.managers.GameManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.util.*

data class Game(
  val gameConfig: GameConfig,
  val players: MutableList<Player>,
  val world: World = Bukkit.getWorld("world")!!,
){
  private var lastWaveEndTicksElapsed: Long = 0
  private var currentWaveIndex: Int = 0
  private val waves: List<GameWave> = gameConfig.waves.map { GameWave(it) }
  private val waveEntities = mutableListOf<GameWaveEntity>()
  private val spawnedTowers = mutableListOf<GameTower>()
  lateinit var loop: BukkitTask
  private val playerMoney = mutableMapOf<UUID, Int>()
  private val playerUiBossBars = mutableMapOf<UUID, BossBar>()
  fun getPlayerMoney(player: Player) = playerMoney[player.uniqueId] ?: 0
  fun addPlayerMoney(player: Player, amount: Int){
    playerMoney[player.uniqueId] = (playerMoney[player.uniqueId] ?: 0) + amount
  }
  fun addMoneyReward(amount: Int){
    players.forEach { addPlayerMoney(it, amount / players.size) }
  }

  fun broadcast(message: String) {
    Bukkit.getOnlinePlayers().forEach { player -> player.sendMessage(message) }
  }

  val progress get() = currentWave.steps.sumOf { it.spawned } / currentWave.config.steps.sumOf { it.amount }.toDouble()

  fun hasEntityInTowers(entity: Entity) = spawnedTowers.any { it.entity == entity }
  fun hasEntityInWaves(entity: Entity) = waveEntities.any { it.entity == entity }
  fun getTowerFromEntity(entity: Entity) = spawnedTowers.find { it.entity == entity }
  fun getWaveEntityFromEntity(entity: Entity) = waveEntities.find { it.entity == entity }

  private val waveChangeTimeout get() = gameConfig.waveChangeTimeout
  private val isCurrentWaveEnded get() = waves.getOrNull(currentWaveIndex)?.allSpawned == true && waveEntities.isEmpty()
  private val isGameEnded get() = isCurrentWaveEnded && currentWaveIndex + 1 >= waves.size
  private val currentWave get() = waves[currentWaveIndex]

  init {
    players.forEach { player -> playerMoney[player.uniqueId] = gameConfig.startingMoney }
  }

  private fun summonNextMob(){
    if(isCurrentWaveEnded) {
      if(lastWaveEndTicksElapsed == 0L) {
        addMoneyReward(currentWave.config.endReward)
      }
      lastWaveEndTicksElapsed++
      if(lastWaveEndTicksElapsed > waveChangeTimeout) {
        currentWaveIndex++
        lastWaveEndTicksElapsed = 0
      }
      return
    }
    waveEntities.addAll(currentWave.spawnNextMob(this))
  }

  private fun moveMobs(){
    for (mob in waveEntities) {
      mob.move()
    }
  }

  fun hasTower(position: Position) = spawnedTowers.any { it.position == position }

  fun spawnOrGetTower(position: Position, towerConf: GameTowerConfig = gameConfig.towers[0], player: Player): GameTower? {
    spawnedTowers.removeIf { tower -> tower.entity.isDead || !tower.entity.isValid }
    var tower = spawnedTowers.find { it.position == position }
    if(tower != null) return tower
    if(getPlayerMoney(player) < towerConf.levelCosts[0]) {
      player.sendMessage("You don't have enough money to buy this tower, you need ${towerConf.levelCosts[0]} but you have ${getPlayerMoney(player)}")
      return null
    }
    tower = GameTower(towerConf, this, position, world, 0)
    spawnedTowers.add(tower)
    addPlayerMoney(player, -towerConf.levelCosts[0])
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
        showUi()
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
    playerUiBossBars.values.forEach { it.removeAll() }
    playerUiBossBars.clear()
  }

  fun playerLeave(player: Player){
    players.remove(player)
    if(players.isEmpty()) stop()
  }

  fun playerJoin(player: Player){
    players.add(player)
    player.teleport(gameConfig.gameRoomSpawn!!.toLocation(world))
    addPlayerMoney(player, gameConfig.startingMoney)
  }

  fun getBossBarTitle(player: Player) = "Money: ${getPlayerMoney(player)} | Wave: ${currentWaveIndex + 1}/${waves.size} | Progress: ${progress * 100}% | Mobs left: ${waveEntities.size}"
  fun showUi(){
    players.forEach {
      var bb = playerUiBossBars[it.uniqueId]
      if(bb == null) bb = Bukkit.createBossBar(getBossBarTitle(it), BarColor.GREEN, BarStyle.SOLID).apply { addPlayer(it) }
      bb!!.progress = progress
      bb.title = getBossBarTitle(it)
      playerUiBossBars[it.uniqueId] = bb
    }
  }
}