package fr.not_here.not_tower_defense.config.models

data class GameWaveStepConfig(
  var mobName: String = "zombie_lv1",
  var amount: Int = 10,
  var cooldown: Int = 5,
  var timeout: Int = 0,
  var strengthMultiplier: Double = 1.0,
  var canSpawnWithPrev: Boolean = false,
)
