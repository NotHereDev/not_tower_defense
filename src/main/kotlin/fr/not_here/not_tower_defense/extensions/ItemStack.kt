@file:JvmName("ItemStackExtensions")
package fr.not_here.not_tower_defense.extensions

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

val ItemStack.glowing: ItemStack get() {
    val meta = this.itemMeta
    meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true)
    meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS)
    this.itemMeta = meta
    return this
}