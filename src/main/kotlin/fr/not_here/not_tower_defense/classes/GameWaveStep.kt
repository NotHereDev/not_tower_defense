package fr.not_here.not_tower_defense.classes

import org.bukkit.World

data class GameWaveStep(
  val entity: GameEntityData,
  val timeout: Int,
  val delay: Int,
  val count: Int,
  var spawned: Int = 0,
  var timeoutPassed: Boolean = false,
) {
  val allSpawned: Boolean get() = spawned >= count

  fun spawnNextMob(spawnRoom: Zone, targetZone: Zone, world: World): GameEntity? {
    if (allSpawned) return null
    spawned++
    return GameEntity(spawnRoom.centerGround, targetZone.centerGround, world, entity)
  }
}