package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.NotTowerDefense
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.util.Vector
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

object Particles {
  fun rayCast(from: Location, to: Location, particle: Particle, particleCount: Int = 20, color: Int = 0xFFFFFF) {
    val distance = from.distance(to)
    from.setDirection(to.toVector().subtract(from.toVector()))
    val step = distance / particleCount
    for (i in 0..particleCount) {
      val loc = from.add(from.direction.multiply(step))
      var red = (color and 0xFF).toDouble() / 255
      if(red == 0.0) red = Float.MIN_VALUE.toDouble()
      from.world.spawnParticle(particle, loc.x, loc.y, loc.z, 0, red, ((color shr 8) and 0xFF).toDouble() / 255, ((color shr 16) and 0xFF).toDouble() / 255, 1.0)
    }
  }

  fun disc(location: Location, particle: Particle, particleCount: Int = 20, color: Int = 0xFFFFFF, radius: Double = 3.0) {
    val step = 360 / particleCount
    for(radius in 0..radius.roundToInt()){
      for (i in 0..particleCount) {
        val loc = location.clone().add(Vector(radius * cos((i * step).toDouble()), 0.0, radius * sin((i * step).toDouble())))
        var red = (color and 0xFF).toDouble() / 255
        if(red == 0.0) red = Float.MIN_VALUE.toDouble()
        location.world.spawnParticle(particle, loc.x, loc.y, loc.z, 0, red, ((color shr 8) and 0xFF).toDouble() / 255, ((color shr 16) and 0xFF).toDouble() / 255, 1.0)
      }
    }
  }
}