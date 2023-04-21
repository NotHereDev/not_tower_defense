package fr.not_here.not_tower_defense.menu.start

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.classes.Game
import fr.not_here.not_tower_defense.classes.Position
import fr.not_here.not_tower_defense.config.containers.GamesConfigContainer
import fr.not_here.not_tower_defense.extensions.addAll
import fr.not_here.not_tower_defense.extensions.fill
import fr.not_here.not_tower_defense.extensions.glowing
import fr.not_here.not_tower_defense.extensions.head
import fr.not_here.not_tower_defense.managers.GameManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.UUID

class SelectTowerMenuHolder(val player: Player, val game: Game, val position: Position): CustomMenuHolder {
  override fun getInventory() = inv

  private val inv: Inventory = Bukkit.createInventory(this, 3*9, "Start Menu")
  private val towers = game.gameConfig.towers.associateBy { it.name }

  override fun onEvent(event: InventoryEvent){
    when(event){
      is InventoryDragEvent -> {
        event.isCancelled = true
      }
      is InventoryClickEvent -> {
        event.isCancelled = true
        onClick(event.slot, event.currentItem)
      }
      is InventoryOpenEvent -> onOpen()
    }
  }

  private fun onClick(slot: Int, item: ItemStack?){
    if(item == null) return
    when(slot){
      in 9*1+1..9*1+7 -> {selectTower(slot, item)}
    }
  }

  fun selectTower(slot: Int, item: ItemStack){
    @Suppress("NAME_SHADOWING") var slot = slot
    if(towers.size % 2 == 0 && slot == 9*1+4) return
    if(towers.size % 2 == 0 && slot > 9*1+4) slot--

    val tower = towers.values.elementAt(slot - 9*1 - 1 - (3-towers.size/2))

    game.spawnOrGetTower(position, tower)
  }

  private fun onOpen(){
    inv.fill(ItemStack(Material.STAINED_GLASS_PANE, 1, 15.toShort()))
    NotTowerDefense.instance.logger.info("Opening menu for ${player.name}")
    NotTowerDefense.instance.logger.info("Opening towers: ${towers.keys}")
    val items = towers.map { ItemStack(it.value.displayItemEnum).apply { itemMeta = itemMeta.apply { displayName = it.value.displayName } } }
    if(items.size % 2 == 0) {
      inv.addAll(items.subList(0, items.size/2), 9*1+1 + (3-items.size/2))
      inv.addAll(items.subList(items.size/2, items.size), 9*1+4)
    } else {
      inv.addAll(items, 9*1+1 + (3-items.size/2))
    }
  }
}