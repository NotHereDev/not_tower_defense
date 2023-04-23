package fr.not_here.not_tower_defense.managers

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.classes.Game
import fr.not_here.not_tower_defense.classes.GameTower
import fr.not_here.not_tower_defense.classes.GameWaveEntity
import fr.not_here.not_tower_defense.classes.Position
import fr.not_here.not_tower_defense.config.containers.GamesConfigContainer
import fr.not_here.not_tower_defense.config.containers.GlobalConfigContainer
import fr.not_here.not_tower_defense.config.models.GameConfig
import fr.not_here.not_tower_defense.config.models.GamePowerConfig
import jdk.nashorn.internal.objects.Global
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object GameManager {
  var games: MutableList<Game> = mutableListOf()

  fun isPlayerInGame(player: Player): Boolean {
    return games.any { it.players.contains(player) }
  }
  fun isTowerEntity(entity: Entity): Boolean {
    return games.any { it.hasEntityInTowers(entity) }
  }
  fun isWaveEntity(entity: Entity): Boolean {
    return games.any { it.hasEntityInWaves(entity) }
  }
  fun getGameTowerFromEntity(entity: Entity): GameTower? {
    return games.find { it.hasEntityInTowers(entity) }?.getTowerFromEntity(entity)
  }
  fun getGameWaveEntityFromEntity(entity: Entity): GameWaveEntity? {
    return games.find { it.hasEntityInWaves(entity) }?.getWaveEntityFromEntity(entity)
  }
  fun getRunningGame(name: String): Game? {
    return games.find { it.gameConfig.name == name }
  }

  fun getRunningGames(name: String): List<Game> {
    return games.filter { it.gameConfig.name == name }
  }

  fun countRunningGames(name: String): Int {
    return getRunningGames(name).size
  }

  fun findAvailableOffsetsFromGameConfig(gameConfig: GameConfig): List<Position> {
    val result = mutableListOf<Position>()
    for(i in 0 until gameConfig.maxArenaCount){
      val offset = gameConfig.arenaOffset * i.toDouble()
      if(!games.any { it.gameOffset == offset }){
        result += offset
      }
    }
    return result
  }

  fun getOffsetFromGameConfig(gameConfig: GameConfig): Position {
    return findAvailableOffsetsFromGameConfig(gameConfig).first()
  }

  fun canStartGame(gameConfig: GameConfig): Boolean{
    return countRunningGames(gameConfig.name) < gameConfig.maxArenaCount
  }

  fun getGameConfigFromWorldName(player: Player): GameConfig {
    val worldName = player.location.world.name
    val gameName = GlobalConfigContainer.instance!!.worldGameConfig[worldName]
      ?: return GamesConfigContainer.instance!!.games!!.first()
    return GamesConfigContainer.instance!!.games!!.find { it.name == gameName }!!
  }

  fun startGame(player: Player, power: GamePowerConfig, sendMessages: Boolean = false, askedPlayers: List<Player> = listOf()) {
    if(games.any { it.players.contains(player) }){
      if(sendMessages) player.sendMessage(Message.gameAlreadyStarted())
      return
    }
    if(!canStartGame(getGameConfigFromWorldName(player))){
      if(sendMessages) player.sendMessage(Message.tooMuchGameStarted())
      return
    }
    games += Game(getGameConfigFromWorldName(player), mutableListOf(player), player.world, power, getOffsetFromGameConfig(getGameConfigFromWorldName(player)), askedPlayers)
    games.last().run()
    if(sendMessages) player.sendMessage(Message.gameStarted())
  }

  fun getGame(player: Player, sendMessages: Boolean): Game? {
    val game = games.find { it.players.contains(player) }
    if(game == null){
      if(sendMessages) player.sendMessage(Message.notInGame())
    }
    return game
  }

  fun stopGame(player: Player, sendMessages: Boolean = false) {
    val game = getGame(player, sendMessages) ?: return
    game.stop()
    games.remove(game)
    if(sendMessages) player.sendMessage(Message.gameStopped())
  }

  fun restartGame(player: Player, power: GamePowerConfig, sendMessages: Boolean = false) {
    stopGame(player, sendMessages)
    startGame(player, power, sendMessages)
  }

  fun startOrRestartGame(player: Player, power: GamePowerConfig, sendMessages: Boolean = false) {
    if(games.any { it.players.contains(player) }){
      restartGame(player, power, sendMessages)
      return
    }

    startGame(player, power, sendMessages)
  }
}