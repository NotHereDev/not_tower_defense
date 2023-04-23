package fr.not_here.not_tower_defense.menu.edit_tower

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.classes.Game
import fr.not_here.not_tower_defense.classes.GameTower
import fr.not_here.not_tower_defense.classes.Position
import fr.not_here.not_tower_defense.config.containers.GlobalConfigContainer
import fr.not_here.not_tower_defense.config.models.GameTowerConfig
import fr.not_here.not_tower_defense.extensions.addAll
import fr.not_here.not_tower_defense.extensions.fill
import fr.not_here.not_tower_defense.managers.Message
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
    updateSellDisplay()
  }

  fun getDisplayItemForLevel(level: Int, cost: Int?, levelCount: Int): MenuItem{
    val item = GlobalConfigContainer.instance!!.towerLevelDisplayItems.takeLast(levelCount).getOrNull(level-1) ?: Material.BARRIER.name
    return MenuItem(
      Material.getMaterial(item),
      name=GlobalConfigContainer.instance!!.levelPattern.replace("{level}", level.toString())
    ).apply {
      lore = mutableListOf(GlobalConfigContainer.instance!!.costPattern.replace("{cost}", cost?.toString() ?: "--"));
      onClick = { onTryUpgradeTower(level) }; if(cost == null) glow;
    }
  }

  override fun onOpen(){
    if(gameTower.owner != player){
      player.sendMessage(Message.towerNotYours())
      player.closeInventory()
      return
    }
    fill(MenuItem(Material.STAINED_GLASS_PANE, 1, 15.toShort()))
    updateTowerLevelDisplay()
    updateTowerAimDisplay()
    updateTowerModifiersDisplay()
    updateSellDisplay()
  }

  fun updateTowerLevelDisplay(){
    val levels = gameTower.levelCosts
    fill(levels.map { getDisplayItemForLevel(it.key, it.value, levels.size) }, 5 - levels.size/2, 2, levels.size, 1)
  }

  fun updateTowerAimDisplay(){
    val levels = gameTower.levelCosts
    setItem(
      MenuItem(GlobalConfigContainer.instance!!.aimClosestDisplayEnum, name=GlobalConfigContainer.instance!!.aimClosestDisplayText)
        .apply { onClick = { gameTower.aimStrongest = false; updateTowerAimDisplay() }; if(!gameTower.aimStrongest) glow },
      2, 3)
    setItem(
      MenuItem(GlobalConfigContainer.instance!!.aimStrongestDisplayEnum, name=GlobalConfigContainer.instance!!.aimStrongestDisplayText)
        .apply { onClick = { gameTower.aimStrongest = true; updateTowerAimDisplay() }; if(gameTower.aimStrongest) glow },
      8, 3)
  }

  fun updateTowerModifiersDisplay(){
    val mods = gameTower.config.towerModifiers
    if(mods.isNullOrEmpty()) return
    fill(
      mods.map {
        MenuItem(
          it.displayItemEnum,
          name=it.displayName,
          lore=mutableListOf(GlobalConfigContainer.instance!!.costPattern.replace("{cost}", it.levelCosts[0].toString())).apply { addAll(it.description) }
        ).apply {
          onClick = { gameTower.selectModifier(mods.indexOf(it), player); onOpen(); updateSellDisplay() }
        }
      },
      5 - mods.size/2, 4, mods.size, 1
    )
  }

  fun updateSellDisplay(){
    setItem(
      MenuItem(
        GlobalConfigContainer.instance!!.sellDisplayEnum,
        name=GlobalConfigContainer.instance!!.sellDisplayText,
        lore=mutableListOf(GlobalConfigContainer.instance!!.sellPattern.replace("{sell}", gameTower.getSellPrice().toString()))
      )
        .apply { onClick = { gameTower.sell(player); player.closeInventory() } },
      5, 5
    )
  }
}