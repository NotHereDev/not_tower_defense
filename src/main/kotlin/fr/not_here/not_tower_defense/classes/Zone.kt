package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.extensions.isBetween
import org.bukkit.Bukkit

data class Zone(
  var from: Position = Position(0.0, 0.0, 0.0),
  var to: Position = Position(0.0, 0.0, 0.0)
){
  operator fun plus(other: Zone): Zone {
    return Zone(
      Position(
        from.x+ other.from.x,
        from.y+ other.from.y,
        from.z+ other.from.z
      ),
      Position(
        to.x+ other.to.x,
        to.y+ other.to.y,
        to.z+ other.to.z
      )
    )
  }

  operator fun plus(other: Position): Zone {
    return Zone(
      Position(
        from.x+ other.x,
        from.y+ other.y,
        from.z+ other.z
      ),
      Position(
        to.x+ other.x,
        to.y+ other.y,
        to.z+ other.z
      )
    )
  }

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