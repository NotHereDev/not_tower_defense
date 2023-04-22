package fr.not_here.not_tower_defense.config.containers

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.ConfigLoader
import fr.not_here.not_tower_defense.config.models.GameConfig
import fr.not_here.not_tower_defense.config.models.GamePowerConfig

data class PowersConfigContainer(
    var powers: List<GamePowerConfig>? = null
) {
    fun check() {
        if(powers == null) throw IllegalArgumentException("Powers config cannot be null")
        if(powers!!.isEmpty()) throw IllegalArgumentException("Powers config cannot be empty")
        powers?.forEach { it.check() }
    }
    companion object {
        @JvmStatic
        var instance: PowersConfigContainer? = null
            private set

        fun load() {
            instance = null
            NotTowerDefense.instance.logger.info("§3Loading powers config...")
            val configContainerInstance = ConfigLoader.loadConfig<PowersConfigContainer>("powers/powers")
            if(configContainerInstance == null) {
                NotTowerDefense.instance.logger.severe("§4Config cannot be loaded")
            } else {
                NotTowerDefense.instance.logger.info("§bLoaded powers config.")
                configContainerInstance.check()
                instance = configContainerInstance
            }
        }
    }
}
