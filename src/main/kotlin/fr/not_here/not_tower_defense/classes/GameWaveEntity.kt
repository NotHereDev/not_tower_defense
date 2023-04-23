package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.config.containers.GlobalConfigContainer
import fr.not_here.not_tower_defense.config.models.GameMobConfig
import fr.not_here.not_tower_defense.enums.MobAttack
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import kotlin.math.roundToInt

class GameWaveEntity (val config: GameMobConfig, val relatedGame: Game, strengthMultiplier: Double = 1.0) {
  var step: Int = 0
  val entity: Entity
  private var hasBeenOrientated: Boolean = false
  var slowDownTimer: Int = 0
  var slowDownPower: Double = 0.0

  val slowDown: Double get() {
    if(slowDownTimer <= 0) return 0.0
    slowDownTimer --
    return slowDownPower
  }

  val position: Position
    get() = Position(entity.location.blockX.toDouble(), entity.location.blockY.toDouble(), entity.location.blockZ.toDouble())

  val targetIsEnd get() = step >= relatedGame.gameConfig.pathSteps.size
  val targetZone get() = relatedGame.gameConfig.pathSteps.getOrNull(step) ?: relatedGame.gameConfig.endRoom
  val targetPosition get() = relatedGame.gameConfig.pathSteps.getOrNull(step)?.centerGround ?: endPosition
  val lastTargetedPosition get() = relatedGame.gameConfig.pathSteps.getOrNull(step - 1)?.centerGround ?: spawnPosition
  val spawnPosition get() = relatedGame.gameConfig.startRoom.centerGround
  val endPosition get() = relatedGame.gameConfig.endRoom.centerGround

  init {
    entity = relatedGame.world.spawnEntity((spawnPosition + Position.randomize(1.0) * Position(1.0, 0.0, 1.0)).toLocation(relatedGame.world).setDirection(targetPosition.toVector().subtract(spawnPosition.toVector())), config.entityTypeEnum)
    if(entity is LivingEntity){
      entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue = config.health * strengthMultiplier
      entity.health = config.health * strengthMultiplier
      entity.isCollidable = false
      entity.setAI(true)
      entity.isCustomNameVisible = true
    }
    entity.customName = config.name + "[${config.health * strengthMultiplier} / ${config.health * strengthMultiplier}]"
    if(entity is LivingEntity){
      entity.customName = GlobalConfigContainer.instance!!.waveEntityNamePattern
        .replace("{mobName}", config.name)
        .replace("{health}", entity.health.toInt().toString())
        .replace("{maxHealth}", entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue?.roundToInt().toString())
    }
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
    if(targetIsEnd && isInZone(targetZone)) {
      relatedGame.damageGameHealth(config.damage)
      return entity.remove()
    }
    if(!hasBeenOrientated) {
      orientate(targetZone)
      hasBeenOrientated = true
    }
    if(isInZone(targetZone)) {
      step++
      hasBeenOrientated = false
    }
    if(slowDown > 0.0) {
      Particles.disc(entity.location, Particle.REDSTONE, 10, 0x333333, 1.0)
      entity.velocity = position.pullVelocity(targetPosition, config.speed).multiply(1 - slowDown)
    } else {
      entity.velocity = position.pullVelocity(targetPosition, config.speed)
    }

    for (attack in config.attacks) {
      if(entity.ticksLived % attack.delay != 0) continue
      val targetTower = entity.getNearbyEntities(attack.range, attack.range, attack.range)
        .filter { relatedGame.getTowers().map { t->t.entity }.contains(it) }
        .minByOrNull { it.location.distance(entity.location) } ?: continue
      when(attack.attackEnum){
        MobAttack.DOWNGRADE_TOWER -> {
          relatedGame.getTowerFromEntity(targetTower)?.downgradeTimes(attack.power.toInt())
        }
        MobAttack.POWER_DOWN_TOWER -> {
          relatedGame.getTowerFromEntity(targetTower)?.apply { powerDownTimer = attack.duration.toInt(); powerDownPower = attack.power }
        }
        MobAttack.STUN_TOWER -> {
          relatedGame.getTowerFromEntity(targetTower)?.stunTimer = attack.duration.toInt()
        }
      }
      Particles.rayCast(entity.location, targetTower.location, Particle.REDSTONE, 20, 0x333333)
    }
  }

  fun orientate(targetZone: Zone){
    val target = targetZone.centerGround.toVector()
    entity.teleport(entity.location.setDirection(target.subtract(entity.location.toVector())))
  }
}