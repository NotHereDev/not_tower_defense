@file:JvmName("PlayerExtensions")
package fr.not_here.not_tower_defense.extensions

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta


val Player.head: ItemStack get() {
    val head = ItemStack(Material.SKULL_ITEM, 1, SkullType.PLAYER.ordinal.toShort())
    val meta = head.itemMeta as SkullMeta
    meta.owningPlayer = Bukkit.getOfflinePlayer(this.uniqueId)
    meta.displayName = this.displayName
    head.itemMeta = meta
    return head
}