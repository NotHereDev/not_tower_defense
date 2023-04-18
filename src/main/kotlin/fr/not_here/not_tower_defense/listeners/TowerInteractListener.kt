package fr.not_here.not_tower_defense.listeners

import fr.not_here.not_tower_defense.classes.Position
import fr.not_here.not_tower_defense.commands.RunCommand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

class TowerInteractListener: Listener {
  @EventHandler
  fun onPlayerInteractBlock(event: PlayerInteractEvent){
    val block = event.clickedBlock ?: return;
    val gameInstance = RunCommand.gameInstances.find { it.players.contains(event.player) } ?: return

    if(block.type != gameInstance.towerOnBlockType && gameInstance.gameZone.contains(Position.fromLocation(block.location))) return
    val tower = gameInstance.spawnOrGetTower(Position.fromLocation(block.location))

    event.player.sendMessage("Tower created at ${tower.position}")
  }

  @EventHandler
  fun onPlayerInteractEntity(event: PlayerInteractEntityEvent){
    val entity = event.rightClicked
    val gameInstance = RunCommand.gameInstances.find { it.players.contains(event.player) } ?: return

    val tower = gameInstance.towers.find { it.entity == entity } ?: return

    event.player.sendMessage("Tower clicked at ${tower.position}")
  }
}