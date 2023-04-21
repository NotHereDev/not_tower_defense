package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.config.models.GameTowerConfig
import fr.not_here.not_tower_defense.enums.TowerType
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import kotlin.math.roundToInt

class GameTower(val config: GameTowerConfig, val position: Position, val world: World, var level: Int = 0){
  lateinit var entity: Entity
  private var target: LivingEntity? = null
  private var lastShot: Int = 0

  fun spawnEntity() {
    entity = world.spawnEntity(position.toLocation(world).add(0.5,1.0,0.5), config.entityTypeEnum)
    if(entity is LivingEntity){
      (entity as LivingEntity).isCollidable = false
      (entity as LivingEntity).setAI(false)
      entity.isCustomNameVisible = true
      entity.setGravity(false)
      entity.isInvulnerable = true
    }
    entity.customName = config.displayName
  }

  init {
    spawnEntity()
  }

  fun targetNearestEntity(entities: List<Entity>){
    if(entity !is LivingEntity) spawnEntity()
    target = entity.getNearbyEntities(config.levelRanges[level],config.levelRanges[level],config.levelRanges[level]).filterIsInstance<LivingEntity>().filter { entities.contains(it) }.minByOrNull { it.location.distance(entity.location) }
    if(target != null) entity.teleport(entity.location.setDirection(target!!.location.add(0.0,target!!.height/2,0.0).toVector().subtract(entity.location.toVector())))
  }

  fun shotNearestEntity(entities: List<Entity>){
    if(lastShot < config.levelShotCooldowns[level]){
      lastShot++
      return
    }
    lastShot = 0
    targetNearestEntity(entities)
    if(target == null || entity !is LivingEntity) return
    when(config.towerTypeEnum){
      TowerType.RAY -> {
        damage(target!!)
        rayCast(target!!)
      }
      TowerType.AOE -> {
        damage(findEntitiesAround(target!!, entities))
        rayCast(target!!)
        disc(target!!)
      }
      TowerType.EXPLOSION -> {
        damage(findEntitiesAround(target!!, entities))
        rayCast(target!!)
        explode(target!!)
      }
      TowerType.SPARK -> {
        val entities = findEntitiesNextEachOther(target!!, entities)
        damage(entities.subList(1, entities.size))
        for(i in entities.indices){
          rayCast(entities[i], if(i==0) entity else entities[i-1], i == 0)
        }
      }
    }
  }

  private fun damage(entity: Entity?){
    if(entity !is LivingEntity) return
    entity.damage(config.levelDamages[level], this.entity)
  }
  private fun damage(entities: List<Entity?>){
    entities.forEach { damage(it) }
  }


  fun findEntitiesNextEachOther(target: Entity, entitiesFilter: List<Entity>): List<Entity>{
    val entities = mutableListOf<Entity?>(target)
    for(i in 0..config.aoeRange.roundToInt()){
      entities.add(entities[i]!!.getNearbyEntities(config.aoeRange,config.aoeRange,config.aoeRange).filterIsInstance<LivingEntity>().filter { entitiesFilter.contains(it) }.minByOrNull { it.location.distance(entities[i]!!.location) })
    }
    return entities.filterNotNull()
  }

  fun findEntitiesAround(target: Entity, entitiesFilter: List<Entity>): List<Entity>{
    return target.getNearbyEntities(config.aoeRange,config.aoeRange,config.aoeRange).apply { add(target) }.filterIsInstance<LivingEntity>().filter { entitiesFilter.contains(it) }
  }

  fun explode(target: Entity){
    target.world.createExplosion(target.location.x, target.location.y, target.location.z, 4F, false, false)
  }

  fun disc(target: Entity){
    Particles.disc(target.location, Particle.REDSTONE, 20, config.particleColor, config.aoeRange)
  }

  fun rayCast(target: Entity, source: Entity = entity, fromEye: Boolean = true){
    Particles.rayCast(source.location.add(0.0, if(fromEye && source is LivingEntity) source.eyeHeight else source.height/2,0.0), target.location.add(0.0,target.height /2,0.0), Particle.REDSTONE, 20, config.particleColor)
  }
}