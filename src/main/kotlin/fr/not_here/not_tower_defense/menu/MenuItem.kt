package fr.not_here.not_tower_defense.menu

import fr.not_here.not_tower_defense.NotTowerDefense
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.SkullType
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class MenuItem(material: Material, count: Int = 1, damage: Short = 0, name: String? = null, lore: MutableList<String>? = null): ItemStack(material, count, damage) {
  var onClick: (MenuItem.() -> Unit)? = null

  val isGlowing: Boolean
    get() = itemMeta.hasEnchant(org.bukkit.enchantments.Enchantment.DURABILITY)
  val glow: MenuItem
    get() {
    itemMeta = itemMeta.apply {
      addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true)
      addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS)
    }
    return this
  }

  val unGlow: MenuItem
    get() {
    itemMeta = itemMeta.apply {
      removeEnchant(org.bukkit.enchantments.Enchantment.DURABILITY)
      removeItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS)
    }
    return this
  }

  var damage: Short
    get() = this.durability
    set(value) {
      if(itemMeta is SkullMeta) return
      this.durability = value
    }

  var count: Int
    get() = this.amount
    set(value) {
      this.amount = value
    }

  var material: Material
    get() = this.type
    set(value) {
      this.type = value
    }

  var name = ""
    set(value) {
      if(type == Material.AIR) return
      itemMeta = itemMeta?.apply {
        displayName = value
      }
      field = value
    }

  var lore: MutableList<String>?
    set(value) {
      itemMeta = itemMeta?.apply {
        lore = value
      }
    }
    get() {
      return itemMeta.lore
    }

  init {
    this.name = name ?: " "
    NotTowerDefense.instance.logger.info("Setting lore of item $name to $lore")
    this.lore = lore
  }

  constructor(offlinePlayer: OfflinePlayer, name: String? = null, _lore: MutableList<String>? = null): this(Material.SKULL_ITEM, 1, SkullType.PLAYER.ordinal.toShort()) {
    itemMeta = (itemMeta as SkullMeta).apply {
      owningPlayer = offlinePlayer
      lore = _lore
    }
    this.name = name ?: offlinePlayer.name
  }
}