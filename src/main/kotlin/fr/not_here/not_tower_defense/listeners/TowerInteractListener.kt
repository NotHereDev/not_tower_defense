package fr.not_here.not_tower_defense.listeners

import fr.not_here.not_tower_defense.classes.GameTower
import fr.not_here.not_tower_defense.classes.Position
import fr.not_here.not_tower_defense.managers.GameManager
import fr.not_here.not_tower_defense.managers.Message
import fr.not_here.not_tower_defense.menu.edit_tower.EditTowerMenuHolder
import fr.not_here.not_tower_defense.menu.select_tower.SelectTowerMenuHolder
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

class TowerInteractListener: Listener {
  @EventHandler
  fun onPlayerInteractBlock(event: PlayerInteractEvent){
    val block = event.clickedBlock ?: return;
    val game = GameManager.getGame(event.player, false) ?: return

    if(!game.gameConfig.gameRoom.contains(Position.fromLocation(block.location))) return
    if(block.type != game.gameConfig.towerOnBlockMaterial) return
    if(game.hasTower(Position.fromLocation(block.location)))
      return towerInteract(event.player, Position.fromLocation(block.location),
        game.spawnOrGetTower(Position.fromLocation(block.location), player=event.player) ?: return
      )

    event.player.openInventory(SelectTowerMenuHolder(event.player, game, Position.fromLocation(block.location)).inventory)
  }

  @EventHandler
  fun onPlayerInteractEntity(event: PlayerInteractEntityEvent){
    val entity = event.rightClicked
    val game = GameManager.getGame(event.player, false) ?: return

    val tower = game.getTowerFromEntity(entity) ?: return

    towerInteract(event.player, tower.position, tower)
  }

  private fun towerInteract(player: Player, position: Position, tower: GameTower){
    if(tower.owner != player){
      player.sendMessage(Message.towerNotYours())
      return
    }
    player.openInventory(EditTowerMenuHolder(player, tower).inventory)
  }
}