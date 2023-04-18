package fr.not_here.not_tower_defense.commands

import fr.not_here.not_tower_defense.classes.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

class StopCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
    if(sender !is Player) {
      sender.sendMessage("You must be a player to run this command")
      return false
    }
    val gameInstance = RunCommand.gameInstances.find { it.players.contains(sender) }
    if(gameInstance != null) {
      sender.sendMessage("Stopping game...")
      gameInstance.stop()
      RunCommand.gameInstances.remove(gameInstance)
    }
    return true
  }
}