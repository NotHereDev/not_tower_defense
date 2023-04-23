package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.containers.GlobalConfigContainer
import fr.not_here.not_tower_defense.config.models.GameConfig
import fr.not_here.not_tower_defense.config.models.GamePowerConfig
import fr.not_here.not_tower_defense.config.models.GameTowerConfig
import fr.not_here.not_tower_defense.extensions.with
import fr.not_here.not_tower_defense.managers.GameManager
import fr.not_here.not_tower_defense.managers.Message
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class Game(
  val _gameConfig: GameConfig,
  val players: MutableList<Player>,
  val world: World = Bukkit.getWorld("world")!!,
  val selectedPower: GamePowerConfig,
  val gameOffset: Position = Position(0.0, 0.0, 0.0),
  val askedPlayers: List<Player> = listOf()
){
  var gameConfigCache: GameConfig? = null

  val gameConfig: GameConfig get() {
    if(gameConfigCache == null) {
      gameConfigCache = _gameConfig.copy()
      gameConfigCache!!.applyOffset(gameOffset)
    }
    return gameConfigCache!!
  }
  private var lastWaveEndTicksElapsed: Long = 0
  private var currentWaveIndex: Int = 0
  private val waves: List<GameWave> = gameConfig.waves.map { GameWave(it) }
  private val waveEntities = mutableListOf<GameWaveEntity>()
  private val spawnedTowers = mutableListOf<GameTower>()
  lateinit var loop: BukkitTask
  private val playerMoney = mutableMapOf<UUID, Int>()
  private val playerUiWaveBossBars = mutableMapOf<UUID, BossBar>()
  private val playerUiHealthBossBar = mutableMapOf<UUID, BossBar>()
  private var health: Double = gameConfig.health
  private var heroPower: Double = 0.0
  private var heroPowerTimer: Int = 0
  private var playerOriginLocation: MutableMap<UUID, Location> = mutableMapOf()

  fun addHeroPower(amount: Double) { heroPower += amount }
  fun usePower() {
    if(heroPowerTimer > 0) {
      players.forEach { it.sendMessage(Message.powerNotCharged("seconds" to heroPowerTimer/20)) }
      return
    }
    if(heroPower < selectedPower.cost) {
      players.forEach { it.sendMessage(Message.notEnoughHeroPowerToUsePower("heroPower" to selectedPower.cost)) }
      return
    }
    heroPower -= selectedPower.cost
    heroPowerTimer = selectedPower.cooldown
    if(selectedPower.lightningPower > 0) {
      for (entity in waveEntities) {
        if(entity.entity !is LivingEntity) continue
        entity.entity.world.strikeLightningEffect(entity.entity.location)
        entity.entity.damage(selectedPower.lightningPower, players.first())
      }
    }
    if(selectedPower.healGame > 0) healGameHealth(selectedPower.healGame.roundToInt())
    if(selectedPower.slowDownPower > 0) {
      for (entity in waveEntities) {
        entity.slowDownTimer = selectedPower.slowDownDuration
        entity.slowDownPower = selectedPower.slowDownPower
      }
    }
    if(selectedPower.tpMobsBack) {
      for (entity in waveEntities) {
        entity.tpBack()
      }
    }
    if(selectedPower.tpMobsStart) {
      for (entity in waveEntities) {
        entity.tpBackToSpawn()
      }
    }
    if(selectedPower.towerDamageMultiplier > 0) {
      for(tower in spawnedTowers) {
        tower.powerDownPower  = 1 / selectedPower.towerDamageMultiplier
        tower.powerDownTimer = selectedPower.towerDamageMultiplierDuration
      }
    }
  }

  fun damageGameHealth(amount: Double){
    health -= amount
    if(health <= 0) {
      players.forEach { player ->
        player.sendTitle(
          GlobalConfigContainer.instance!!.looseTitle,
          GlobalConfigContainer.instance!!.looseSubtitle.with("waveNumber" to currentWaveIndex),
          10,
          40,
          10
        )
      }
      stop()
    }
  }

  fun healGameHealth(amount: Int) { health += amount }

  fun getWaveEntities() = waveEntities
  fun getTowers() = spawnedTowers
  fun removeTower(tower: GameTower){
    spawnedTowers.remove(tower)
    tower.entity.remove()
  }
  fun getPlayerMoney(player: Player) = playerMoney[player.uniqueId] ?: 0
  fun addPlayerMoney(player: Player, amount: Int){
    playerMoney[player.uniqueId] = (playerMoney[player.uniqueId] ?: 0) + amount
  }
  fun addMoneyReward(amount: Int){
    players.forEach { addPlayerMoney(it, amount / players.size) }
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

    if(tower != null) {
      if(tower.owner != player){
        player.sendMessage(Message.towerNotYours())
        return null
      }
      return tower
    }
    if(getPlayerMoney(player) < towerConf.levelCosts[0]) {
      player.sendMessage(Message.notEnoughMoneyToBuyTower("money" to towerConf.levelCosts[0], "currentMoney" to getPlayerMoney(player)))
      return null
    }
    tower = GameTower(towerConf, this, position, world, player, 0)
    spawnedTowers.add(tower)
    addPlayerMoney(player, -towerConf.levelCosts[0])
    return tower
  }

  fun shotMobs(){
    for (tower in spawnedTowers) {
      tower.shotNearestEntity(waveEntities.map { it.entity })
    }
  }

  fun updatePlayerInventories(ignoreTicksLived: Boolean = false){
    players.filter { it.ticksLived % 200 == 0 || ignoreTicksLived }.forEach { player ->
      player.inventory.clear()
      player.inventory.setItem(4, selectedPower.representativeItemStack )
    }
  }

  fun run() : BukkitTask {
    players.forEach { player ->
      playerOriginLocation[player.uniqueId] = player.location.clone()
      player.teleport(gameConfig.gameRoomSpawn!!.toLocation(world).setDirection(gameConfig.gameRoom.centerGround.toVector().subtract(gameConfig.gameRoomSpawn!!.toVector()))
    ) }
    if(!::loop.isInitialized || loop.isCancelled) {
      world.entities?.filter { it !is Player }?.filter { gameConfig.gameRoom.contains(Position.fromLocation(it.location)) }?.forEach { it.remove() }
      updatePlayerInventories(true)
      loop = Bukkit.getScheduler().runTaskTimer(NotTowerDefense.instance, {
        updatePlayerInventories()
        waveEntities.removeIf { mob -> mob.entity.isDead || !mob.entity.isValid }
        if(isGameEnded) {
          players.forEach { player ->
            player.sendTitle(
              GlobalConfigContainer.instance!!.winTitle,
              GlobalConfigContainer.instance!!.winSubtitle,
              10,
              40,
              10
            )
          }
          stop()
          GameManager.games.remove(this)
          return@runTaskTimer
        }
        if(heroPowerTimer > 0) heroPowerTimer--
        summonNextMob()
        moveMobs()
        shotMobs()
        showUi()
      }, 0, 1)
    }
    return loop
  }

  fun stop() {
    waveEntities.forEach { mob ->
      if(mob.entity.isValid && !mob.entity.isDead)
        mob.entity.remove()
    }
    waveEntities.clear()
    spawnedTowers.forEach { tower ->
      if(tower.entity.isValid && !tower.entity.isDead)
      tower.entity.remove()
    }
    spawnedTowers.clear()
    Bukkit.getScheduler().cancelTask(loop.taskId)
    if(!::loop.isInitialized && !loop.isCancelled) loop.cancel()
    playerUiWaveBossBars.values.forEach { it.removeAll() }
    playerUiWaveBossBars.clear()
    playerUiHealthBossBar.values.forEach { it.removeAll() }
    playerUiHealthBossBar.clear()
    GameManager.games.remove(this)
    players.forEach { player ->
      player.inventory.clear()
      if(playerOriginLocation.contains(player.uniqueId)) {
        player.teleport(playerOriginLocation[player.uniqueId])
      } else {
        player.teleport(player.world.spawnLocation)
      }
    }
  }

  fun playerLeave(player: Player){
    players.remove(player)
    if(players.isEmpty()) stop()
    if(player.isOnline) {
      if (playerOriginLocation.contains(player.uniqueId)) {
        player.teleport(playerOriginLocation[player.uniqueId])
      } else {
        player.teleport(player.world.spawnLocation)
      }
    }
  }

  fun playerJoin(player: Player){
    if(players.contains(player)) return
    players.add(player)
    playerOriginLocation[player.uniqueId] = player.location.clone()
    player.teleport(gameConfig.gameRoomSpawn!!.toLocation(world))
    addPlayerMoney(player, gameConfig.startingMoney)
  }
  fun getWaveBossBarTitle(player: Player) = GlobalConfigContainer.instance!!.waveBossBarTitle
    .replace("{money}", getPlayerMoney(player).toString())
    .replace("{heroPower}", heroPower.roundToInt().toString())
    .replace("{waveNumber}", "${currentWaveIndex + 1}")
    .replace("{totalWaveNumber}", "${waves.size}")
    .replace("{waveProgress}", "${(progress * 10000).roundToInt()/100}")
    .replace("{mobsLeft}", "${waveEntities.size}")

  fun getHealthBossBarTitle(player: Player) = GlobalConfigContainer.instance!!.healthBossBarTitle
    .replace("{health}", "$health")
    .replace("{maxHealth}", "${gameConfig.health}")

  fun showUi(){
    players.forEach {
      var bb = playerUiWaveBossBars[it.uniqueId]
      if(bb == null) bb = Bukkit.createBossBar(getWaveBossBarTitle(it), GlobalConfigContainer.instance!!.waveBossBarColorEnum, BarStyle.SOLID).apply { addPlayer(it) }
      bb!!.progress = max(min(progress, 1.0),0.0)
      bb.title = getWaveBossBarTitle(it)
      playerUiWaveBossBars[it.uniqueId] = bb

      bb = playerUiHealthBossBar[it.uniqueId]
      if(bb == null) bb = Bukkit.createBossBar(getHealthBossBarTitle(it), GlobalConfigContainer.instance!!.healthBossBarColorEnum, BarStyle.SOLID).apply { addPlayer(it) }
      bb!!.progress = max(min(health / gameConfig.health, 1.0),0.0)
      bb.title = getHealthBossBarTitle(it)
      playerUiHealthBossBar[it.uniqueId] = bb
    }
  }
}