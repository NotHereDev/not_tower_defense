package fr.not_here.not_tower_defense.menu.edit_tower

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.classes.Game
import fr.not_here.not_tower_defense.classes.GameTower
import fr.not_here.not_tower_defense.classes.Position
import fr.not_here.not_tower_defense.config.containers.GlobalConfigContainer
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

class EditTowerMenuHolder(val player: Player, val gameTower: GameTower): CustomMenuHolder() {
  override fun getInventory() = inv

  private val inv: Inventory = Bukkit.createInventory(this, 5*9, "Start Menu")

  override fun onClick(slot: Int, item: ItemStack?){}

  fun onTryUpgradeTower(level: Int){
    gameTower.tryLevelUp(player, level)
    updateTowerLevelDisplay()
  }

  fun getDisplayItemForLevel(level: Int, cost: Int?, levelCount: Int): MenuItem{
    val item = GlobalConfigContainer.instance!!.towerLevelDisplayItems.takeLast(levelCount).getOrNull(level-1) ?: Material.BARRIER.name
    return MenuItem(Material.getMaterial(item), name="Level ${level}").apply { lore = mutableListOf("Cost: ${cost ?: "--"}"); onClick = { onTryUpgradeTower(level) }; if(cost == null) glow; }
  }

  override fun onOpen(){
    fill(MenuItem(Material.STAINED_GLASS_PANE, 1, 15.toShort()))
    updateTowerLevelDisplay()
  }

  fun updateTowerLevelDisplay(){
    val levels = gameTower.levelCosts
    fill(levels.map { getDisplayItemForLevel(it.key, it.value, levels.size) }, 5 - levels.size/2, 2, levels.size, 1)
  }
}