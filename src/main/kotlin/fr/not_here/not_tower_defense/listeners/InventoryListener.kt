package fr.not_here.not_tower_defense.listeners

import fr.not_here.not_tower_defense.managers.GameManager
import fr.not_here.not_tower_defense.menu.CustomMenuHolder
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent

class InventoryListener: Listener {
  @EventHandler
  fun onEvent(event: InventoryEvent){
    if(event.inventory.holder is CustomMenuHolder) (event.inventory.holder as CustomMenuHolder).onEvent(event)
  }
  @EventHandler
  fun onclickEvent(event: InventoryClickEvent){
    if(event.inventory.holder is CustomMenuHolder) (event.inventory.holder as CustomMenuHolder).onEvent(event)
    if(event.whoClicked !is Player) return
    GameManager.getGame(event.whoClicked as Player, false) ?: return
    event.isCancelled = true
  }
  @EventHandler
  fun onDragEvent(event: InventoryDragEvent){
    if(event.inventory.holder is CustomMenuHolder) (event.inventory.holder as CustomMenuHolder).onEvent(event)
    if(event.whoClicked !is Player) return
    GameManager.getGame(event.whoClicked as Player, false) ?: return
    event.isCancelled = true
  }
  @EventHandler
  fun onCloseEvent(event: InventoryCloseEvent){
    if(event.inventory.holder is CustomMenuHolder) (event.inventory.holder as CustomMenuHolder).onEvent(event)
  }
  @EventHandler
  fun onOpenEvent(event: InventoryOpenEvent){
    if(event.inventory.holder is CustomMenuHolder) (event.inventory.holder as CustomMenuHolder).onEvent(event)
  }

  @EventHandler
  fun onUseItem(event: PlayerInteractEvent) {
    if(!event.hasItem()) return
    val game = GameManager.getGame(event.player, false) ?: return
    if(game.selectedPower.representativeItemStack.isSimilar(event.item)) {
      game.usePower()
      event.isCancelled = true
    }
  }

  @EventHandler
  fun onDrop(event: PlayerDropItemEvent){
    GameManager.getGame(event.player, false) ?: return
    event.isCancelled = true
  }
}