package fr.not_here.not_tower_defense.menu.select_tower

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.classes.Game
import fr.not_here.not_tower_defense.classes.Position
import fr.not_here.not_tower_defense.config.models.GameTowerConfig
import fr.not_here.not_tower_defense.extensions.addAll
import fr.not_here.not_tower_defense.extensions.fill
import fr.not_here.not_tower_defense.menu.CustomMenuHolder
import fr.not_here.not_tower_defense.menu.MenuItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class SelectTowerMenuHolder(val player: Player, val game: Game, val position: Position): CustomMenuHolder() {
  override fun getInventory() = inv

  private val inv: Inventory = Bukkit.createInventory(this, 3*9, "Start Menu")
  private val towers = game.gameConfig.towers

  override fun onClick(slot: Int, item: ItemStack?){}

  fun selectTower(towerConfig: GameTowerConfig){
    game.spawnOrGetTower(position, towerConfig, player)
    player.closeInventory()
  }

  override fun onOpen(){
    fill(MenuItem(Material.STAINED_GLASS_PANE, 1, 15.toShort()))
    val items = towers.map { MenuItem(it.displayItemEnum, name=it.displayName).apply { onClick = { selectTower(it) }; lore = mutableListOf("Cost: ${it.levelCosts[0]}").apply { addAll(it.description) } } }
    if(items.size % 2 == 0) {
      fill(items.subList(0, items.size/2), 2 + (3-items.size/2),2,(3-items.size/2),1)
      fill(items.subList(items.size/2, items.size), 6,2,(3-items.size/2),1)
    } else {
      fill(items, 5 - items.size/2, 2, items.size, 1)
    }
  }
}