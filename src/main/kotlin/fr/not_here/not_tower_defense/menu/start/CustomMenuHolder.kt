package fr.not_here.not_tower_defense.menu.start

import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.InventoryHolder

interface CustomMenuHolder: InventoryHolder {
  fun onEvent(event: InventoryEvent)
}