package fr.not_here.not_tower_defense.classes

import com.sun.org.apache.xpath.internal.operations.Bool
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import kotlin.math.atan2
import kotlin.math.sqrt

class GameEntity(positionInit: Position, targetPosition: Position, world: World, val data: GameEntityData) {
  var step: Int = 0
  val entity: Entity
  private var hasBeenOrientated: Boolean = false

  val position: Position
    get() = Position(entity.location.blockX.toDouble(), entity.location.blockY.toDouble(), entity.location.blockZ.toDouble())

  init {
    entity = world.spawnEntity(positionInit.toLocation(world).setDirection(targetPosition.toVector().subtract(positionInit.toVector())), data.type)
    if(entity is LivingEntity){
      entity.health = data.health
      entity.isCollidable = false
      entity.setAI(true)
      entity.isCustomNameVisible = true
    }
    entity.customName = data.name + "[${data.health} / ${data.health}]"
  }

  fun isInZone(zone: Zone): Boolean {
    return zone.contains(position)
  }

  fun move(targetZone: Zone, isEndZone: Boolean){
    if(isEndZone && isInZone(targetZone)) return entity.remove()
    if(!hasBeenOrientated) {
      orientate(targetZone)
      hasBeenOrientated = true
    }
    if(isInZone(targetZone)) {
      step++
      hasBeenOrientated = false
    }
    val velocity = position.pullVelocity(targetZone.centerGround, data.speed)
    entity.velocity = velocity
  }

  fun orientate(targetZone: Zone){
    val target = targetZone.centerGround.toVector()
    entity.teleport(entity.location.setDirection(target.subtract(entity.location.toVector())))
  }
}