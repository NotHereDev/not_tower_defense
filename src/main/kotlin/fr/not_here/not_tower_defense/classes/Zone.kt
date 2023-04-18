package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.extensions.isBetween
import org.bukkit.Bukkit

data class Zone(
  val from: Position,
  val to: Position
){
  val center: Position
    get() = Position(
      (from.x + (to.x - from.x) /2) + 0.5,
      (from.y + (to.y - from.y) /2),
      (from.z + (to.z - from.z) /2) + 0.5
    )
  val centerGround: Position
    get() = Position(
      (from.x + (to.x - from.x) /2) + 0.5,
      from.y.coerceAtMost(to.y),
      (from.z + (to.z - from.z) /2) + 0.5
    )


  fun contains(position: Position): Boolean {
    return position.x.isBetween(from.x, to.x)
      && position.y.isBetween(from.y, to.y)
      && position.z.isBetween(from.z, to.z);
  }
}