package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.config.models.GameMobConfig
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity

class GameWaveEntity (val config: GameMobConfig, val relatedGame: Game, strengthMultiplier: Double = 1.0) {
  var step: Int = 0
  val entity: Entity
  private var hasBeenOrientated: Boolean = false

  val position: Position
    get() = Position(entity.location.blockX.toDouble(), entity.location.blockY.toDouble(), entity.location.blockZ.toDouble())

  val targetIsEnd get() = step >= relatedGame.gameConfig.pathSteps.size
  val targetZone get() = relatedGame.gameConfig.pathSteps.getOrNull(step) ?: relatedGame.gameConfig.endRoom
  val targetPosition get() = relatedGame.gameConfig.pathSteps.getOrNull(step)?.centerGround ?: endPosition
  val lastTargetedPosition get() = relatedGame.gameConfig.pathSteps.getOrNull(step - 1)?.centerGround ?: spawnPosition
  val spawnPosition get() = relatedGame.gameConfig.startRoom.centerGround
  val endPosition get() = relatedGame.gameConfig.endRoom.centerGround

  init {
    entity = relatedGame.world.spawnEntity(spawnPosition.toLocation(relatedGame.world).setDirection(targetPosition.toVector().subtract(spawnPosition.toVector())), config.entityTypeEnum)
    if(entity is LivingEntity){
      entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue = config.health * strengthMultiplier
      entity.health = config.health * strengthMultiplier
      entity.isCollidable = false
      entity.setAI(true)
      entity.isCustomNameVisible = true
    }
    entity.customName = config.name + "[${config.health * strengthMultiplier} / ${config.health * strengthMultiplier}]"
  }

  fun tpBackToSpawn(){
    step = 0
    entity.teleport(spawnPosition.toLocation(relatedGame.world).setDirection(targetPosition.toVector().subtract(position.toVector())))
  }

  fun tpBack(){
    if(step <= 0) return tpBackToSpawn()
    entity.teleport(lastTargetedPosition.toLocation(relatedGame.world).setDirection(targetPosition.toVector().subtract(position.toVector())))
  }

  fun isInZone(zone: Zone): Boolean {
    return zone.contains(position)
  }

  fun move(){
    if(targetIsEnd && isInZone(targetZone)) return entity.remove()
    if(!hasBeenOrientated) {
      orientate(targetZone)
      hasBeenOrientated = true
    }
    if(isInZone(targetZone)) {
      step++
      hasBeenOrientated = false
    }
    val velocity = position.pullVelocity(targetPosition, config.speed)
    entity.velocity = velocity
  }

  fun orientate(targetZone: Zone){
    val target = targetZone.centerGround.toVector()
    entity.teleport(entity.location.setDirection(target.subtract(entity.location.toVector())))
  }
}