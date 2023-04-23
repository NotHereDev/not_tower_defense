package fr.not_here.not_tower_defense.config.containers

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.commands.TdCommand
import fr.not_here.not_tower_defense.config.ConfigLoader
import fr.not_here.not_tower_defense.extensions.with
import org.bukkit.Material
import org.bukkit.boss.BarColor


data class MessagesConfigContainer(
  var mustBePlayer: String = "§cYou must be a player to execute this command",
  var notInGame: String = "§cYou are not in a game",
  var gameStopped: String = "§cGame stopped",
  var gameStarted: String = "§aGame started",
  var gameAlreadyStarted: String = "§cYou are already in a game, type /td stop to stop it",
  var tooMuchGameStarted: String = "§cThere are already too much games running, please wait a bit",
  var playerNotFound: String = "§cPlayer {player} not found",
  var invalidPower: String = "§cInvalid power {power}, valid ones are: {powers}",
  var noPowerSpecified: String = "§cYou must specify a power to start a game, valid powers are: {powers}",
  var concurrentGameAsks: String = "You have been asked to join multiple games, please specify which one you want to join with the name of one of players who asked you: {players}",
  var noGameAsk: String = "§cYou have not been asked to join any game, or the game you have been asked to join does not exist anymore",
  var gameAsked: String = "§aYou have been asked to join a game by {player}, type /td join to join it, or click accept below:",
  var accept: String = "§aAccept",
  var commandDoesNotExist: String = "§cThis command does not exist, type /td help to see the list of commands",

  var tdHelpHeader: String = "§bNot Tower Defense help",
  var tdHelp: String = "§b/td - Show the game starting UI",
  var tdHelpHelp: String = "§b/td help §7- Show this help",
  var tdStartHelp: String = "§b/td start <power> [player1] [player2] ... §7- Start a game with the specified power, and ask the specified players to join",
  var tdStopHelp: String = "§b/td stop §7- Stop the game you are in",
  var tdJoinHelp: String = "§b/td join [player] §7- Join a game you have been asked to join, player is required if you have been asked to join multiple games",

  var powerNotCharged: String = "§cYou cannot use this power, it is not charged yet, please wait {seconds} seconds",
  var notEnoughHeroPowerToUsePower: String = "§cYou cannot use this power, you do not have enough hero power, you need {heroPower} hero power to use this power",
  var towerNotYours: String = "§cThis tower, is not yours",
  var notEnoughMoneyToBuyTower: String = "§cYou cannot buy this tower, you do not have enough money, you need {money} to buy this tower, you have {currentMoney}",
  var notEnoughMoneyToUpgradeTower: String = "§cYou cannot upgrade this tower, you do not have enough money, you need {money} to upgrade this tower, you have {currentMoney}",
  var towerMaxLevel: String = "§cThis tower is already at its maximum level",
  var leveledUpTower: String = "§aYou have leveled up this tower to level {level}, costed {money}",
  var boughtTower: String = "§aYou have bought this tower, costed {money}",
  var cantSellTower: String = "§cYou cannot sell this tower, it is not yours",
  var soldTower: String = "§aYou have sold this tower, you have been refunded {money}",
  var mustUpgradeTowerBeforeModifying: String = "§cYou must upgrade this tower before modifying it",
  var notEnoughMoneyToBuyModifier: String = "§cYou cannot buy this modifier, you do not have enough money, you need {money} to buy this modifier, you have {currentMoney}",
  var boughtModifier: String = "§aYou have bought this modifier, costed {money}",
) {


  companion object {
    @JvmStatic
    var instance: MessagesConfigContainer? = null
      private set

    fun load() {
      instance = null
      NotTowerDefense.instance.logger.info("§3Loading messages config...")
      val configContainerInstance = ConfigLoader.loadConfig<MessagesConfigContainer>("messages")
      if(configContainerInstance == null) {
        NotTowerDefense.instance.logger.severe("§4Config cannot be loaded")
      } else {
        NotTowerDefense.instance.logger.info("§bLoaded messages config.")
        instance = configContainerInstance
      }
    }
  }
}
