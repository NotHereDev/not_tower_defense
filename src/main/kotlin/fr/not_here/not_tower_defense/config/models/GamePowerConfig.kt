package fr.not_here.not_tower_defense.config.models

import fr.not_here.not_tower_defense.config.containers.GlobalConfigContainer
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


data class GamePowerConfig (
  var name: String = "ligthningPower",
  var displayName: String = "§6Ligthning Power",
  var description: List<String> = listOf("§7Strike all mobs of the game for §650000 §7damage."),
  var displayItem: String = Material.NETHER_STAR.name,
  var lightningPower: Double = 0.0,
  var slowDownPower: Double = 0.0,
  var slowDownDuration: Int = 100,
  var towerDamageMultiplier: Double = 0.0,
  var towerDamageMultiplierDuration: Int = 100,
  var tpMobsBack: Boolean = false,
  var tpMobsStart: Boolean = false,
  var healGame: Double = 0.0,
  var cost: Int = 1000,
  var cooldown: Int = 12000,
) {

  val displayItemEnum: Material get() = Material.valueOf(displayItem)

  val representativeItemStack get() = ItemStack(displayItemEnum).apply {
    itemMeta = itemMeta.also {
      it.displayName = displayName
      it.lore = mutableListOf(GlobalConfigContainer.instance!!.costPattern.replace("{cost}", cost.toString())).apply { addAll(description) }
    }
  }

  fun check(){
    if (lightningPower < 0.0) {
      throw IllegalArgumentException("Invalid ligthningPower $lightningPower, on config path: powers.ligthningPower, must be greater than 0.0")
    }
    if(slowDownPower < 0.0 || slowDownPower > 1.0) {
      throw IllegalArgumentException("Invalid slowDownPower $slowDownPower, on config path: powers.slowDownPower, must be between 0.0 and 1.0")
    }
    if(slowDownDuration < 0) {
      throw IllegalArgumentException("Invalid slowDownDuration $slowDownDuration, on config path: powers.slowDownDuration, must be greater than 0")
    }
    if(towerDamageMultiplier < 0.0) {
      throw IllegalArgumentException("Invalid towerDamageMultiplier $towerDamageMultiplier, on config path: powers.towerDamageMultiplier, must be between 0.0 and 1.0")
    }
    if(towerDamageMultiplierDuration < 0) {
      throw IllegalArgumentException("Invalid towerDamageMultiplierDuration $towerDamageMultiplierDuration, on config path: powers.towerDamageMultiplierDuration, must be greater than 0")
    }
    if(healGame < 0.0) {
      throw IllegalArgumentException("Invalid healGame $healGame, on config path: powers.healGame, must be greater than 0.0")
    }
    if(cost < 0.0) {
      throw IllegalArgumentException("Invalid cost $cost, on config path: powers.cost, must be greater than 0.0")
    }
    if(displayItem !in Material.values().map { it.name }) {
      throw IllegalArgumentException("Invalid displayItem $displayItem, on config path: powers.displayItem, must be a valid Material name")
    }
  }
}