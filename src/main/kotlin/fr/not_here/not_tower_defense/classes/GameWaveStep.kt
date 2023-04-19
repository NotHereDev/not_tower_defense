package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.models.GameWaveStepConfig

class GameWaveStep(val config: GameWaveStepConfig) {
  var lastSpawned = 0
  var spawned = 0
  val allSpawned get() = spawned >= config.amount
  val canSpawn get() = lastSpawned >= config.cooldown && !allSpawned
  val canSpawnWithPrevious get() = canSpawn && config.spawnWithPrevious
  val timeout get() = config.timeout

  fun spawnNextMob(relatedGame: Game, spawnRoom: Zone, targetZone: Zone): GameWaveEntity? {
    lastSpawned++
    if (!canSpawn) return null
    spawned++
    lastSpawned = 0
    return GameWaveEntity(
      relatedGame.gameConfig.mobs.find { it.name == config.mobName }!!,
      relatedGame, spawnRoom.centerGround, targetZone.centerGround, config.strengthMultiplier
    )
  }
}