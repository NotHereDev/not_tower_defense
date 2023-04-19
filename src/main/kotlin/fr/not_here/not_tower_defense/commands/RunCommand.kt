package fr.not_here.not_tower_defense.commands

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.classes.*
import fr.not_here.not_tower_defense.config.containers.GamesConfigContainer
import fr.not_here.not_tower_defense.config.models.GameConfig
import fr.not_here.not_tower_defense.managers.GameManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

class RunCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
    if(sender !is Player) {
      sender.sendMessage("You must be a player to run this command")
      return false
    }

    val name = args.getOrNull(0) ?: GamesConfigContainer.instance?.games?.firstOrNull()?.name ?: return false

    GameManager.startOrRestartGame(name, sender, true)

    return true
  }
}