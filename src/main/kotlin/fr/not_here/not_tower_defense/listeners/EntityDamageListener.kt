package fr.not_here.not_tower_defense.listeners

import fr.not_here.not_tower_defense.config.containers.GlobalConfigContainer
import fr.not_here.not_tower_defense.managers.GameManager
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityExplodeEvent
import kotlin.math.roundToInt

class EntityDamageListener: Listener {
  @EventHandler
  fun onEntityDamagedByEntity(event: EntityDamageByEntityEvent) {
    val entity = GameManager.getGameWaveEntityFromEntity(event.entity) ?: return
    if(entity.entity is LivingEntity) {
      entity.entity.noDamageTicks = 0
      event.isCancelled = true
      (event.entity as LivingEntity).damage(event.finalDamage)
      entity.entity.customName = GlobalConfigContainer.instance!!.waveEntityNamePattern
        .replace("{mobName}", entity.config.name)
        .replace("{health}", (entity.entity.health - event.finalDamage).roundToInt().toString())
        .replace("{maxHealth}", entity.entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue?.roundToInt().toString())
    }
  }

  @EventHandler
  fun onEntityDeath(event: EntityDeathEvent) {
    if (!GameManager.isWaveEntity(event.entity) && !GameManager.isTowerEntity(event.entity)) return
    event.droppedExp = 0
    event.drops.clear()
  }

  @EventHandler
  fun onEntityExplode(event: EntityDamageEvent) {
    if (!GameManager.isWaveEntity(event.entity) && !GameManager.isTowerEntity(event.entity)) return
    listOf(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, EntityDamageEvent.DamageCause.BLOCK_EXPLOSION).forEach {
      if (event.cause == it) event.isCancelled = true
    }
  }
}