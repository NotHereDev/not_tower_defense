package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.models.GameTowerConfig
import fr.not_here.not_tower_defense.enums.TowerType
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

class GameTower(private val _config: GameTowerConfig, val relatedGame: Game, val position: Position, val world: World, var level: Int = 0){
  lateinit var entity: Entity
  private var target: LivingEntity? = null
  private var lastShot: Int = 0
  private val modifierPath = mutableListOf<Int>()
  var stunTimer = 0
  var powerDownTimer = 0
  var powerDownPower = 1.0
  val entityDamageTimes = mutableMapOf<UUID, Int>()

  fun getDamageStackMultiplierBonus(entity: Entity): Double {
    if (config.damageUpgradeStack == 1.0 || config.maxDamageUpgradeStack == 1.0) return 1.0
    if(this.entity.ticksLived % 500 == 0){
      val entitiesInGame = relatedGame.getWaveEntities()
      entityDamageTimes.forEach { (uuid, stack) ->
        if(entitiesInGame.none { it.entity.uniqueId == uuid }) entityDamageTimes.remove(uuid)
      }
    }
    val stack = entityDamageTimes[entity.uniqueId] ?: 0
    entityDamageTimes[entity.uniqueId] = stack + 1
    return min(config.damageUpgradeStack.pow(stack), config.maxDamageUpgradeStack)
  }

  val damageReduction: Double get() {
    if(powerDownTimer <= 0) return 1.0
    powerDownTimer --
    return powerDownPower
  }

  val isStuned: Boolean get() {
    if(stunTimer <= 0) return false
    stunTimer --
    return true
  }

  val maxLevel get() = config.startLevel + config.levelCosts.size - 1

  fun downgrade(){
    if(level <= 0) {
      if(modifierPath.isEmpty()) {
        relatedGame.removeTower(this)
      } else {
        modifierPath.removeLast()
        level = maxLevel
      }
    } else {
      level --
    }
    entity.customName = config.displayName
  }

  fun downgradeTimes(times: Int){
    for(i in 0 until times) downgrade()
  }

  val config: GameTowerConfig
    get() {
      var modifier = _config
      for (mp in modifierPath) {
        val mod = modifier.towerModifiers?.getOrNull(mp)
        if(mod == null) return modifier
        else modifier = mod
      }
      return modifier
    }

  fun selectModifier(modifier: Int){
    if(config.towerModifiers?.getOrNull(modifier) == null) return
    modifierPath.add(modifier)
    entity.customName = config.displayName
    level = config.startLevel
  }

  val levelCosts get() = config.run {
    levelCosts.withIndex().associate { it.index to if ((level + startLevel) >= it.index) null else it.value }
  }

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

  private fun targetNearestEntity(entities: List<Entity>){
    if(entity !is LivingEntity) spawnEntity()
    target = entity.getNearbyEntities(config.levelRanges[level],config.levelRanges[level],config.levelRanges[level]).filterIsInstance<LivingEntity>().filter { entities.contains(it) }.minByOrNull { it.location.distance(entity.location) }
    if(target != null) entity.teleport(entity.location.setDirection(target!!.location.add(0.0,target!!.height/2,0.0).toVector().subtract(entity.location.toVector())))
  }

  fun shotNearestEntity(entities: List<Entity>){
    if(isStuned) return
    if(config.heroPowerEarnPerTick != 0.0)
      relatedGame.addHeroPower(config.heroPowerEarnPerTick)
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
        damage(findEntitiesNextEachOther(target!!, entities))
        for(i in entities.indices){
          rayCast(entities[i], if(i==0) entity else entities[i-1], i == 0)
        }
      }
    }
  }

  private fun damage(entity: Entity?){
    if(entity !is LivingEntity) return
    val waveEntity = relatedGame.getWaveEntityFromEntity(entity)

    if(waveEntity?.config?.dodgeChance != 0.0 && (waveEntity?.config?.dodgeChance ?: 0.0) > Math.random()) return

    entity.noDamageTicks = 0
    entity.damage((config.levelDamages[level] / damageReduction) * getDamageStackMultiplierBonus(entity), this.entity)

    if(entity.health <= 0.0) relatedGame.addMoneyReward(waveEntity?.config?.reward ?: 0 )

    if(waveEntity?.config?.towerAbilityImunity == true) return

    if(config.fireTicksInflicted != 0) entity.fireTicks = config.fireTicksInflicted

    if(config.slowDownChance != 0.0 && config.slowDownChance > Math.random()) {
      waveEntity?.slowDownTimer = config.slowDownDuration
      waveEntity?.slowDownPower = config.slowDownPower
    }
    if(config.tpBackChance != 0.0 && config.tpBackChance > Math.random()) {
      waveEntity?.tpBack()
    }
    if(config.tpStartChance != 0.0 && config.tpStartChance > Math.random()) {
      waveEntity?.tpBackToSpawn()
    }
  }
  private fun damage(entities: List<Entity?>){
    entities.forEach { damage(it) }
  }


  fun findEntitiesNextEachOther(target: Entity, entitiesFilter: List<Entity>): List<Entity>{
    val entities = mutableListOf<Entity?>(target)
    for(i in 0..config.aoeRange.roundToInt()){
      if(entities.getOrNull(i) == null) break
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

  fun tryLevelUp(player: Player){
    if(level >= maxLevel) {
      player.sendMessage("§cThis tower is already max level!")
      return
    }
    if(relatedGame.getPlayerMoney(player) < config.levelCosts[level+1]) {
      player.sendMessage("§cYou need ${config.levelCosts[level+1]}$ to level up this tower!")
      return
    }
    player.sendMessage("§aYou leveled up this tower to level ${level+1}!, costed ${config.levelCosts[level+1]}")
    relatedGame.addPlayerMoney(player, -config.levelCosts[level+1])
    level++
  }

  fun tryLevelUp(player: Player, targetLevel: Int){
    for(i in level until targetLevel){
      tryLevelUp(player)
    }
  }
}