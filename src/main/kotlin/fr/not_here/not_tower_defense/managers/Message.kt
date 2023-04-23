package fr.not_here.not_tower_defense.managers

import fr.not_here.not_tower_defense.config.containers.MessagesConfigContainer
import fr.not_here.not_tower_defense.extensions.with

object Message{
  fun mustBePlayer(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.mustBePlayer.with(*replaces)
  fun notInGame(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.notInGame.with(*replaces)
  fun gameStopped(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.gameStopped.with(*replaces)
  fun gameStarted(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.gameStarted.with(*replaces)
  fun gameAlreadyStarted(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.gameAlreadyStarted.with(*replaces)
  fun tooMuchGameStarted(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.tooMuchGameStarted.with(*replaces)
  fun playerNotFound(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.playerNotFound.with(*replaces)
  fun invalidPower(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.invalidPower.with(*replaces)
  fun noPowerSpecified(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.noPowerSpecified.with(*replaces)
  fun concurrentGameAsks(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.concurrentGameAsks.with(*replaces)
  fun noGameAsk(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.noGameAsk.with(*replaces)
  fun gameAsked(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.gameAsked.with(*replaces)
  fun accept(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.accept.with(*replaces)
  fun commandDoesNotExist(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.commandDoesNotExist.with(*replaces)
  fun powerNotCharged(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.powerNotCharged.with(*replaces)
  fun notEnoughHeroPowerToUsePower(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.notEnoughHeroPowerToUsePower.with(*replaces)
  fun towerNotYours(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.towerNotYours.with(*replaces)
  fun notEnoughMoneyToBuyTower(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.notEnoughMoneyToBuyTower.with(*replaces)
  fun notEnoughMoneyToUpgradeTower(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.notEnoughMoneyToUpgradeTower.with(*replaces)
  fun towerMaxLevel(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.towerMaxLevel.with(*replaces)
  fun leveledUpTower(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.leveledUpTower.with(*replaces)
  fun boughtTower(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.boughtTower.with(*replaces)
  fun cantSellTower(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.cantSellTower.with(*replaces)
  fun soldTower(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.soldTower.with(*replaces)
  fun mustUpgradeTowerBeforeModifying(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.mustUpgradeTowerBeforeModifying.with(*replaces)
  fun notEnoughMoneyToBuyModifier(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.notEnoughMoneyToBuyModifier.with(*replaces)
  fun boughtModifier(vararg replaces: Pair<String, Any>) = MessagesConfigContainer.instance!!.boughtModifier.with(*replaces)


  fun tdHelp(): List<String>{
    return listOf(
      MessagesConfigContainer.instance!!.tdHelpHeader,
      MessagesConfigContainer.instance!!.tdHelp,
      MessagesConfigContainer.instance!!.tdHelpHelp,
      MessagesConfigContainer.instance!!.tdStartHelp,
      MessagesConfigContainer.instance!!.tdStopHelp,
      MessagesConfigContainer.instance!!.tdJoinHelp
    )
  }

}