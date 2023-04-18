package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.NotTowerDefense
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.util.Vector
import kotlin.math.absoluteValue

object Particles {
  fun rayCast(from: Location, to: Location, particle: Particle, particleCount: Int = 20) {
    val distance = from.distance(to)
    from.setDirection(to.toVector().subtract(from.toVector()))
    val step = distance / particleCount
    for (i in 0..particleCount) {
      from.world.spawnParticle(particle, from.add(from.direction.multiply(step)), 1, .0,.0,.0,.0)
    }
  }
}