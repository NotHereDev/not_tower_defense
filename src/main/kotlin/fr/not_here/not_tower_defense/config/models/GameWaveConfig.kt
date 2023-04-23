package fr.not_here.not_tower_defense.config.models

data class GameWaveConfig (
  var steps: List<GameWaveStepConfig> = listOf(),
  var endReward: Int = 100,
  var timeout: Int = 0
)