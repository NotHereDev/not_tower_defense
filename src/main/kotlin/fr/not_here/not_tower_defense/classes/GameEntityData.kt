package fr.not_here.not_tower_defense.classes

import org.bukkit.entity.EntityType

data class GameEntityData(
  val name: String,
  val type: EntityType,
  val health: Double,
  val speed: Double,
  val damage: Double,
  val armor: Double,
)