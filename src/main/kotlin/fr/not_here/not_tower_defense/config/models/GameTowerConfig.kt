package fr.not_here.not_tower_defense.config.models

import fr.not_here.not_tower_defense.enums.TowerType
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.EntityType

data class GameTowerConfig(
  var name: String = "ray_tower",
  var displayName: String = "§aRay Tower",
  var description: List<String> = listOf("§7A simple ray tower"),
  var entityType: String = EntityType.SKELETON.name,
  var displayItem: String = Material.SKULL_ITEM.name,
  var startLevel: Int = 0,
  var levelCosts: List<Int> = listOf(500, 800, 1500, 3000),
  var levelRanges: List<Double> = listOf(5.0, 6.0, 8.0, 10.0),
  var levelDamages: List<Double> = listOf(1.0, 1.5, 2.0, 4.0),
  var levelShotCooldowns: List<Int> = listOf(12, 10, 8, 6),
  var towerModifiers: List<GameTowerConfig>? = null,
  var towerType: String = TowerType.RAY.name,
  var aoeRange: Double = 2.0,
  var particleType: String = Particle.BLOCK_DUST.name,
  var particleColor: Int = 0xFFFFFF,
  var tpBackChance: Double = 0.0,
  var tpStartChance: Double = 0.0,
  var slowDownChance: Double = 0.0,
  var slowDownDuration: Int = 0,
  var slowDownPower: Double = 0.0,
  var damageUpgradeStack: Double = 1.0,
  var maxDamageUpgradeStack: Double = 1.0,
  var fireTicksInflicted: Int = 0,
  var heroPowerEarnPerTick: Double = 0.0,
){
  val maxLevel: Int
    get() = maxOf(levelCosts.size, levelRanges.size, levelDamages.size, levelShotCooldowns.size)

  val entityTypeEnum: EntityType
    get() = EntityType.valueOf(entityType)

  val particleTypeEnum: Particle
    get() = Particle.valueOf(particleType)

  val towerTypeEnum: TowerType
    get() = TowerType.valueOf(towerType)

  val displayItemEnum: Material
    get() = Material.valueOf(displayItem)

  fun check() {
    if(levelCosts.size != levelRanges.size || levelCosts.size != levelDamages.size || levelCosts.size != levelShotCooldowns.size)
      throw IllegalArgumentException("All levelCosts, levelRange, levelDamage and levelShotCooldown must have the same size to define max upgradable level of tower $name")
    if(entityType !in EntityType.values().map { it.name }) {
      throw IllegalArgumentException("Invalid entityType $entityType, on config path: towers[?].entityType, for tower $name, valid values: ${EntityType.values().map { it.name }}")
    }
    if(particleType !in Particle.values().map { it.name }) {
      throw IllegalArgumentException("Invalid particleType $particleType, on config path: towers[?].particleType, for tower $name, valid values: ${Particle.values().map { it.name }}")
    }
    if(towerType !in TowerType.values().map { it.name }) {
      throw IllegalArgumentException("Invalid towerType $towerType, on config path: towers[?].towerType, for tower $name, valid values: ${TowerType.values().map { it.name }}")
    }
    if (particleColor !in 0..0xFFFFFF) {
      throw IllegalArgumentException("Invalid particleColor $particleColor, on config path: towers[?].particleColor, for tower $name, use color, ex: 0xFFFFFF")
    }
    if(tpBackChance < 0.0 || tpBackChance > 1.0) {
      throw IllegalArgumentException("Invalid tpBackChance $tpBackChance, on config path: towers[?].tpBackChance, for tower $name, must be between 0.0 and 1.0")
    }
    if(tpStartChance < 0.0 || tpStartChance > 1.0) {
      throw IllegalArgumentException("Invalid tpStartChance $tpStartChance, on config path: towers[?].tpStartChance, for tower $name, must be between 0.0 and 1.0")
    }
    if(displayItem !in Material.values().map { it.name }) {
      throw IllegalArgumentException("Invalid displayItem $displayItem, on config path: towers[?].displayItem, for tower $name, valid values: ${Material.values().map { it.name }}")
    }
    if(slowDownChance < 0.0 || slowDownChance > 1.0) {
      throw IllegalArgumentException("Invalid stunChance $slowDownChance, on config path: towers[?].stunChance, for tower $name, must be between 0.0 and 1.0")
    }
    if(slowDownDuration < 0) {
      throw IllegalArgumentException("Invalid stunDuration $slowDownDuration, on config path: towers[?].stunDuration, for tower $name, must be greater than 0")
    }
    if(slowDownPower < 0.0 || slowDownPower > 1.0) {
      throw IllegalArgumentException("Invalid stunPower $slowDownPower, on config path: towers[?].stunPower, for tower $name, must be between 0.0 and 1.0")
    }
    if(damageUpgradeStack < 1.0) {
      throw IllegalArgumentException("Invalid damageUpgradeStack $damageUpgradeStack, on config path: towers[?].damageUpgradeStack, for tower $name, must be greater than 1")
    }
    if(maxDamageUpgradeStack < 1.0) {
      throw IllegalArgumentException("Invalid maxDamageUpgradeStack $maxDamageUpgradeStack, on config path: towers[?].maxDamageUpgradeStack, for tower $name, must be greater than 1")
    }
  }
}
