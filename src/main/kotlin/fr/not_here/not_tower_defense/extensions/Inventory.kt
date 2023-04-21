@file:JvmName("InventoryExtensions")
package fr.not_here.not_tower_defense.extensions

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


fun Inventory.fill(item: ItemStack) {
    for (i in 0 until this.size) {
        this.setItem(i, item)
    }
}

fun Inventory.addAll(items: List<ItemStack>, start: Int) {
    for (i in start until start + items.size) {
        this.setItem(i, items[i - start])
    }
}
