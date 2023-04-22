package fr.not_here.not_tower_defense.managers

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.classes.Game
import fr.not_here.not_tower_defense.classes.GameTower
import fr.not_here.not_tower_defense.classes.GameWaveEntity
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

  fun getGameConfigFromWorldName(player: Player): GameConfig {
    val worldName = player.location.world.name
    val gameName = GlobalConfigContainer.instance!!.worldGameConfig[worldName]
      ?: return GamesConfigContainer.instance!!.games!!.first()
    return GamesConfigContainer.instance!!.games!!.find { it.name == gameName }!!
  }

  fun startGame(player: Player, power: GamePowerConfig, sendMessages: Boolean = false) {
    if(games.any { it.players.contains(player) }){
      if(sendMessages) player.sendMessage("You are already in a game, type /stop to stop it")
      return
    }
    games += Game(getGameConfigFromWorldName(player), mutableListOf(player), player.world, power)
    games.last().run()
    if(sendMessages) player.sendMessage("Game started")
  }

  fun getGame(player: Player, sendMessages: Boolean): Game? {
    val game = games.find { it.players.contains(player) }
    if(game == null){
      if(sendMessages) player.sendMessage("You are not in a game")
    }
    return game
  }

  fun stopGame(player: Player, sendMessages: Boolean = false) {
    val game = getGame(player, sendMessages) ?: return
    game.stop()
    games.remove(game)
    if(sendMessages) player.sendMessage("Game stopped")
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