package fr.not_here.not_tower_defense.listeners

import fr.not_here.not_tower_defense.classes.Position
import fr.not_here.not_tower_defense.commands.RunCommand
import fr.not_here.not_tower_defense.managers.GameManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

class TowerInteractListener: Listener {
  @EventHandler
  fun onPlayerInteractBlock(event: PlayerInteractEvent){
    val block = event.clickedBlock ?: return;
    val game = GameManager.getGame(event.player, false) ?: return

    if(block.type != game.towerOnBlockType && game.gameConfig.gameRoom.contains(Position.fromLocation(block.location))) return
    val tower = game.spawnOrGetTower(Position.fromLocation(block.location))

    event.player.sendMessage("Tower created at ${tower.position}")
  }

  @EventHandler
  fun onPlayerInteractEntity(event: PlayerInteractEntityEvent){
    val entity = event.rightClicked
    val game = GameManager.getGame(event.player, false) ?: return

    val tower = game.getTowerFromEntity(entity) ?: return

    event.player.sendMessage("Tower clicked at ${tower.position}")
  }
}