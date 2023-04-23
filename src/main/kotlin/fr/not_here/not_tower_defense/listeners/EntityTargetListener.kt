package fr.not_here.not_tower_defense.listeners

import fr.not_here.not_tower_defense.managers.GameManager
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityCombustEvent
import org.bukkit.event.entity.EntityTargetEvent

class EntityTargetListener : Listener {
  @EventHandler
  fun onEntityTarget(event: EntityTargetEvent){
    if(!GameManager.isWaveEntity(event.entity)) return
    event.isCancelled = true
  }
}