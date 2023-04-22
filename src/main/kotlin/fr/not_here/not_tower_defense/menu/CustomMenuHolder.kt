package fr.not_here.not_tower_defense.menu

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

abstract class CustomMenuHolder: InventoryHolder {
  val placedItems = mutableMapOf<Int, MenuItem>()

  fun onItemClick(slot: Int): Boolean {
    if (placedItems[slot] == null) return false
    placedItems[slot]!!.onClick?.invoke(placedItems[slot]!!)
    return true
  }

  fun onEvent(event: InventoryEvent) {
    when(event){
      is InventoryDragEvent -> {
        event.isCancelled = true
      }
      is InventoryClickEvent -> {
        event.isCancelled = true
        if(onItemClick(event.slot))
          onClick(event.slot, placedItems[event.slot])
        else onClick(event.slot, event.currentItem)
      }
      is InventoryOpenEvent -> onOpen()
    }
  }

  abstract fun onClick(slot: Int, item: ItemStack?)
  abstract fun onOpen()

  fun toSlot(x: Int, y: Int): Int{
    return (max(1,min(x, 9))-1) + (max(1, min(y, floor(this.inventory.size.toDouble() / 9).roundToInt()))-1) * 9
  }

  fun fill(item: MenuItem, x: Int = 1, y: Int = 1, width: Int = 9, height: Int = floor(this.inventory.size.toDouble() / 9).roundToInt()) {
    for (i in 0 until width) {
      for (j in 0 until height) {
        setItem(item, x+i, y+j)
      }
    }
  }

  fun fill(items: List<MenuItem>, x: Int = 1, y: Int = 1, width: Int = 9, height: Int = floor(this.inventory.size.toDouble() / 9).roundToInt()) {
    for (i in 0 until width) {
      for (j in 0 until height) {
        setItem(items.getOrNull(i+j*width) ?: MenuItem(Material.AIR), x+i, y+j)
      }
    }
  }

  fun stroke(item: MenuItem, x: Int = 1, y: Int = 1, width: Int = 9, height: Int = floor(this.inventory.size.toDouble() / 9).roundToInt()) {
    for (i in 0 until width) {
      setItem(item, x+i, y)
      setItem(item, x+i, y+height-1)
    }
    for (i in 0 until height) {
      setItem(item, x, y+i)
      setItem(item, x+width-1, y+i)
    }
  }

  fun corners(item: MenuItem, x: Int = 1, y: Int = 1, width: Int = 9, height: Int = floor(this.inventory.size.toDouble() / 9).roundToInt(), cornerSize: Int = 2) {
    for (i in 0 until cornerSize) {
      for (j in 0 until cornerSize) {
        setItem(item, x+i, y+j)
        setItem(item, x+width-1-i, y+j)
        setItem(item, x+i, y+height-1-j)
        setItem(item, x+width-1-i, y+height-1-j)
      }
    }
  }

  fun setItem(item: MenuItem, x: Int = 1, y: Int = 1) {
    if(item.type == Material.AIR) return removeItem(x, y)
    placedItems[toSlot(x, y)] = item
    this.inventory.setItem(toSlot(x, y), item)
  }

  fun removeItem(x: Int = 1, y: Int = 1) {
    placedItems.remove(toSlot(x, y))
    this.inventory.setItem(toSlot(x, y), null)
  }
}