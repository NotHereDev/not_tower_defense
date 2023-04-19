package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.config.models.GameTowerConfig
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity

class GameTower(val config: GameTowerConfig, val position: Position, world: World, var level: Int = 0){
  val entity: Entity
  var target: LivingEntity? = null
  private var lastShot: Int = 0

  init {
    entity = world.spawnEntity(position.toLocation(world).add(0.5,1.0,0.5), config.entityTypeEnum)
    if(entity is LivingEntity){
      entity.isCollidable = false
      entity.setAI(false)
      entity.isCustomNameVisible = true
      entity.setGravity(false)
      entity.isInvulnerable = true
    }
    entity?.customName = config.displayName
  }

  fun targetNearestEntity(entities: List<Entity>){
    target = entity.getNearbyEntities(config.levelRanges[level],config.levelRanges[level],config.levelRanges[level]).filterIsInstance<LivingEntity>().filter { entities.contains(it) }.minByOrNull { it.location.distance(entity.location) }
    if(target != null) entity.teleport(entity.location.setDirection(target!!.location.toVector().subtract(entity.location.toVector())))
  }

  fun shotNearestEntity(entities: List<Entity>){
    if(lastShot < config.levelShotCooldowns[level]){
      lastShot++
      return
    }
    lastShot = 0
    targetNearestEntity(entities)
    target?.noDamageTicks = 0
    target?.damage(config.levelDamages[level], entity)
    displayRayParticles()
  }

  fun displayRayParticles(){
    if(target == null || entity !is LivingEntity) return
    Particles.rayCast(entity.location.add(0.0,entity.eyeHeight,0.0), target!!.location.add(0.0,target!!.height /2,0.0), Particle.REDSTONE)
  }

}