package fr.not_here.not_tower_defense.config.models

import org.bukkit.entity.EntityType


data class GameTowerConfig(
  var name: String = "ray_tower",
  var displayName: String = "§aRay Tower",
  var entityType: String = EntityType.SKELETON.name,
  var startLevel: Int = 0,
  var levelCosts: List<Int> = listOf(500, 1500, 3000),
  var levelRanges: List<Double> = listOf(6.0, 8.0, 10.0),
  var levelDamages: List<Double> = listOf(1.0, 2.0, 4.0),
  var levelShotCooldowns: List<Int> = listOf(10, 8, 6),
  var towerModifiers: List<GameTowerConfig>? = null,
){
  val maxLevel: Int
    get() = maxOf(levelCosts.size, levelRanges.size, levelDamages.size, levelShotCooldowns.size)

  val entityTypeEnum: EntityType
    get() = EntityType.valueOf(entityType)

  fun check() {
    if(levelCosts.size != levelRanges.size || levelCosts.size != levelDamages.size || levelCosts.size != levelShotCooldowns.size)
      throw IllegalArgumentException("All levelCosts, levelRange, levelDamage and levelShotCooldown must have the same size to define max upgradable level of tower $name")
    if(entityType !in EntityType.values().map { it.name }) {
      throw IllegalArgumentException("Invalid entityType $entityType, on config path: game.towers[?].entityType, for tower $name")
    }
  }
}
