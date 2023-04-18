package fr.not_here.not_tower_defense.classes

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

data class Position (
  val x: Double,
  val y: Double,
  val z: Double
) {

  override operator fun equals(other: Any?): Boolean {
    return other is Position && (x == other.x && y == other.y && z == other.z)
  }

  fun toLocation(world: World): Location {
    return Location(world, x, y, z)
  }

  fun toVector() = Vector(x, y, z)

  fun pullVelocity(position: Position, speed: Double): Vector {
    val x = position.x - this.x
    val y = position.y - this.y
    val z = position.z - this.z

    val distance = Math.sqrt(x * x + y * y + z * z)
    val velocity = speed / distance

    return Vector(x * velocity, y * velocity, z * velocity)
  }

  companion object{
    fun fromLocation(location: Location): Position {
      return fromVector(location.toVector())
    }

    fun fromVector(vector: Vector): Position {
      return Position(vector.x, vector.y, vector.z)
    }
  }
}