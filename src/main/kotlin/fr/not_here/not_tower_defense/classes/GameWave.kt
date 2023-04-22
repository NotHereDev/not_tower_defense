package fr.not_here.not_tower_defense.classes

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.models.GameWaveConfig

class GameWave (val config: GameWaveConfig) {
  val steps = config.steps.map { GameWaveStep(it) }
  private var lastStepFinishedTicksUntil = 0
  val allSpawned: Boolean get() = steps.all { it.allSpawned }

  fun spawnNextMob(relatedGame: Game): List<GameWaveEntity> {
    if(allSpawned) return listOf()
    var isAfterUnfinished: Boolean? = null
    val mobs = mutableListOf<GameWaveEntity>()
    for (step in steps) {
      if(!step.canSpawnWithPrevious && isAfterUnfinished == false && lastStepFinishedTicksUntil <= step.timeout) {
        lastStepFinishedTicksUntil++
        break
      }
      if(!step.canSpawnWithPrevious && isAfterUnfinished == true) break
      if(isAfterUnfinished == null) isAfterUnfinished = !step.allSpawned
      mobs += step.spawnNextMob(relatedGame) ?: continue
    }
    return mobs
  }
}