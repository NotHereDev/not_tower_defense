package fr.not_here.not_tower_defense.listeners

import fr.not_here.not_tower_defense.NotTowerDefense
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import kotlin.math.roundToInt

class EntityDamageListener: Listener {
  @EventHandler
  fun onEntityDamage(event: EntityDamageByEntityEvent) {
    val entity = event.entity
    if(entity is LivingEntity) {
      entity.noDamageTicks = 0
      val customNameWithHealth = entity.customName?.split("[", "/", "]")?.map { it.trim() }?.filterNot { it.isBlank() }?: return
      NotTowerDefense.instance.logger.info(customNameWithHealth.toString())
      if(customNameWithHealth.size != 3) return
      entity.customName = "${customNameWithHealth[0]} [${(entity.health - event.damage).roundToInt()}/${customNameWithHealth[2]}]"
    }
  }
}