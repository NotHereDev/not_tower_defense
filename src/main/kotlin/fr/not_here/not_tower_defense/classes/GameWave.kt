package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.models.GameWaveConfig

class GameWave (val config: GameWaveConfig) {
  val steps = config.steps.map { GameWaveStep(it) }
  private var waveTimeout = 0
  val allSpawned: Boolean get() = steps.all { it.allSpawned }

  fun spawnNextMob(relatedGame: Game): List<GameWaveEntity> {
    if(waveTimeout <= config.timeout) {
      waveTimeout++
      return listOf()
    }
    if(allSpawned) return listOf()
    var isAfterUnfinished = false
    val mobs = mutableListOf<GameWaveEntity>()
    for (step in steps) {
      if(!step.config.canSpawnWithPrev && isAfterUnfinished) break
      if(!step.stepTimeoutPassed) break
      if(!isAfterUnfinished) isAfterUnfinished = !step.allSpawned
      if(!step.spawnTimeoutPassed) continue
      mobs += step.spawnNextMob(relatedGame) ?: continue
    }
    return mobs
  }
}