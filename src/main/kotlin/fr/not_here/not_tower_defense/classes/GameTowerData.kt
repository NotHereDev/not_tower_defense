package fr.not_here.not_tower_defense.classes

import org.bukkit.entity.EntityType

class GameTowerData (
  val name: String,
  val entityType: EntityType,
  val range: Int,
  val damage: Double,
  val cooldown: Int,
  val cost: Int,
  val startLevel: Int = 0,
  val levelCosts: List<Int>,
  val towerModifier: List<GameTowerData>,
)