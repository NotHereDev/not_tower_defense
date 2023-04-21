package fr.not_here.not_tower_defense.listeners

import fr.not_here.not_tower_defense.menu.start.CustomMenuHolder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.inventory.InventoryOpenEvent

class InventoryListener: Listener {
  @EventHandler
  fun onEvent(event: InventoryEvent){
    if(event.inventory.holder is CustomMenuHolder) (event.inventory.holder as CustomMenuHolder).onEvent(event)
  }
  @EventHandler
  fun onclickEvent(event: InventoryClickEvent){
    if(event.inventory.holder is CustomMenuHolder) (event.inventory.holder as CustomMenuHolder).onEvent(event)
  }
  @EventHandler
  fun onDragEvent(event: InventoryDragEvent){
    if(event.inventory.holder is CustomMenuHolder) (event.inventory.holder as CustomMenuHolder).onEvent(event)
  }
  @EventHandler
  fun onCloseEvent(event: InventoryCloseEvent){
    if(event.inventory.holder is CustomMenuHolder) (event.inventory.holder as CustomMenuHolder).onEvent(event)
  }
  @EventHandler
  fun onOpenEvent(event: InventoryOpenEvent){
    if(event.inventory.holder is CustomMenuHolder) (event.inventory.holder as CustomMenuHolder).onEvent(event)
  }
}