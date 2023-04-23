package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.containers.MobsConfigContainer
import fr.not_here.not_tower_defense.config.models.GameWaveStepConfig

class GameWaveStep(val config: GameWaveStepConfig) {
  var lastSpawned = 0
  var spawned = 0
  var timeout = 0
  val allSpawned get() = spawned >= config.amount

  val spawnTimeoutPassed: Boolean get(){
    if(lastSpawned < config.cooldown) {
      lastSpawned++
      return false
    }
    return true
  }

  val stepTimeoutPassed: Boolean get(){
    if(timeout < config.timeout) {
      timeout++
      return false
    }
    return true
  }

  val timeoutsPassed get() = spawnTimeoutPassed && stepTimeoutPassed
  val canSpawn: Boolean get() {
    return timeoutsPassed && !allSpawned
  }
  val canSpawnWithPrev get() = canSpawn && config.canSpawnWithPrev

  fun spawnNextMob(relatedGame: Game): GameWaveEntity? {
    if (!canSpawn) return null
    spawned++
    lastSpawned = 0
    return GameWaveEntity(
      MobsConfigContainer.instance!!.mobs!!.find { it.name == config.mobName }!!,
      relatedGame, config.strengthMultiplier
    )
  }
}