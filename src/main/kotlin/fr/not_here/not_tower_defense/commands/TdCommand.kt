package fr.not_here.not_tower_defense.commands

import fr.not_here.not_tower_defense.classes.*
import fr.not_here.not_tower_defense.config.containers.PowersConfigContainer
import fr.not_here.not_tower_defense.managers.GameManager
import fr.not_here.not_tower_defense.managers.Message
import fr.not_here.not_tower_defense.menu.start.StartMenuHolder
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import sun.audio.AudioPlayer.player

class TdCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
    if(sender !is Player) {
      sender.sendMessage(Message.mustBePlayer())
      return false
    }

    when (args.getOrNull(0)){
      "stop" -> {
        if(!GameManager.isPlayerInGame(sender)){
          sender.sendMessage(Message.notInGame())
          return false
        }
        GameManager.stopGame(sender)
        sender.sendMessage(Message.gameStopped())
        return true
      }
      "start" -> {
        when (args.getOrNull(1)) {
          in PowersConfigContainer.instance!!.powers!!.map { it.name } -> {
            if(GameManager.isPlayerInGame(sender)){
              sender.sendMessage(Message.gameAlreadyStarted())
              return false
            }
            val askedPlayers = mutableListOf<Player>()
            if(args.size > 2){
              askedPlayers.addAll(args.drop(2).mapNotNull {
                val p = Bukkit.getPlayer(it)
                if(p == null) {
                  sender.sendMessage(Message.playerNotFound("player" to it))
                  return false
                }
                p
              })
            }
            for (askedPlayer in askedPlayers) {
              askedPlayer.sendMessage(Message.gameAsked("player" to sender.name))
              askedPlayer.spigot().sendMessage(
                TextComponent(Message.accept())
                  .apply {
                    clickEvent = ClickEvent(
                      ClickEvent.Action.RUN_COMMAND,
                      "/td join ${sender.name}"
                    )
                  }
              )
            }
            GameManager.startGame(sender, PowersConfigContainer.instance!!.powers!!.first { it.name == args[1] }, true, askedPlayers)
            return true
          }
          null -> {
            sender.sendMessage(Message.noPowerSpecified("powers" to PowersConfigContainer.instance!!.powers!!.joinToString(", ") { it.name }))
            return false
          }
          else -> {
            sender.sendMessage(Message.invalidPower("power" to args[1], "powers" to PowersConfigContainer.instance!!.powers!!.joinToString(", ") { it.name }))
            return false
          }
        }
      }
      "join" -> {
        if(GameManager.isPlayerInGame(sender)){
          sender.sendMessage(Message.gameAlreadyStarted())
          return false
        }
        val gamesWhoAskedHim = GameManager.games.filter { it.askedPlayers.contains(sender) }
        val game = if(gamesWhoAskedHim.size > 1){
          if(args.size < 2){
            sender.sendMessage(Message.concurrentGameAsks("players" to gamesWhoAskedHim.joinToString(", ") { it.askedPlayers.joinToString { it.name } }))
            return false
          }
          gamesWhoAskedHim.firstOrNull { it.players.any { it.name == args[1] } }
        } else {
          gamesWhoAskedHim.firstOrNull()
        }
        if(game == null){
          sender.sendMessage(Message.noGameAsk())
          return false
        }
        game.playerJoin(sender)
        return true
      }
      "help" -> {
        sender.sendMessage(Message.tdHelp().toTypedArray())
        return true
      }
      null -> {
        if(GameManager.isPlayerInGame(sender)){
          sender.sendMessage(Message.gameStarted())
          return false
        }
        sender.openInventory(StartMenuHolder(sender).inventory)
      }
      else -> {
        sender.sendMessage(Message.commandDoesNotExist())
        return false
      }
    }

    return true
  }
}