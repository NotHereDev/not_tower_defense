package fr.not_here.not_tower_defense.listeners

import fr.not_here.not_tower_defense.managers.GameManager
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityCombustEvent

class EntityCombustListener : Listener {
  @EventHandler
  fun onEntityCombust(event: EntityCombustEvent) {
    if(!GameManager.isWaveEntity(event.entity) && !GameManager.isTowerEntity(event.entity)) return
    val entity = event.entity
    if(
      entity is LivingEntity
      && entity.location.block.run {
        lightFromSky > 11
        && type != Material.WATER
        && getRelative(BlockFace.DOWN)?.type != Material.SOUL_SAND
      }
      && entity.equipment?.helmet != null
      && event.duration == 8
    ) event.isCancelled = true
  }
}