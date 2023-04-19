package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.config.models.GameMobConfig
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity

class GameWaveEntity (val config: GameMobConfig, relatedGame: Game, positionInit: Position, targetPosition: Position, strengthMultiplier: Double = 1.0) {
  var step: Int = 0
  val entity: Entity
  private var hasBeenOrientated: Boolean = false

  val position: Position
    get() = Position(entity.location.blockX.toDouble(), entity.location.blockY.toDouble(), entity.location.blockZ.toDouble())

  init {
    entity = relatedGame.world.spawnEntity(positionInit.toLocation(relatedGame.world).setDirection(targetPosition.toVector().subtract(positionInit.toVector())), config.entityTypeEnum)
    if(entity is LivingEntity){
      entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue = config.health * strengthMultiplier
      entity.health = config.health * strengthMultiplier
      entity.isCollidable = false
      entity.setAI(true)
      entity.isCustomNameVisible = true
    }
    entity.customName = config.name + "[${config.health * strengthMultiplier} / ${config.health * strengthMultiplier}]"
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
    val velocity = position.pullVelocity(targetZone.centerGround, config.speed)
    entity.velocity = velocity
  }

  fun orientate(targetZone: Zone){
    val target = targetZone.centerGround.toVector()
    entity.teleport(entity.location.setDirection(target.subtract(entity.location.toVector())))
  }
}