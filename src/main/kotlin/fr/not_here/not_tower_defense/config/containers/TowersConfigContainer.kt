package fr.not_here.not_tower_defense.config.containers

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.ConfigLoader
import fr.not_here.not_tower_defense.config.models.GameMobConfig
import fr.not_here.not_tower_defense.config.models.GameTowerConfig

data class TowersConfigContainer(
    var towers: List<GameTowerConfig>? = null
) {
    companion object {
        @JvmStatic
        var instance: TowersConfigContainer? = null
            private set

        fun load() {
            instance = null
            NotTowerDefense.instance.logger.info("§3Loading towers config...")
            val configContainerInstance = ConfigLoader.loadConfig<TowersConfigContainer>("towers/towers")
            if(configContainerInstance == null) {
                NotTowerDefense.instance.logger.severe("§4Config cannot be loaded")
            } else {
                NotTowerDefense.instance.logger.info("§bLoaded towers config.")
                configContainerInstance.towers?.forEach { it.check() }
                instance = configContainerInstance
            }
        }
    }
}
