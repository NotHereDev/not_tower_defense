package fr.not_here.not_tower_defense.commands

import fr.not_here.not_tower_defense.classes.*
import fr.not_here.not_tower_defense.managers.GameManager
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
    GameManager.stopGame(sender, true)
    return true
  }
}