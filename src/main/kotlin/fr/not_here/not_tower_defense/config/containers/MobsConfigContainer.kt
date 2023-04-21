package fr.not_here.not_tower_defense.config.containers

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.ConfigLoader
import fr.not_here.not_tower_defense.config.models.GameConfig
import fr.not_here.not_tower_defense.config.models.GameMobConfig

data class MobsConfigContainer(
    var mobs: List<GameMobConfig>? = null
) {
    companion object {
        @JvmStatic
        var instance: MobsConfigContainer? = null
            private set

        fun load() {
            instance = null
            NotTowerDefense.instance.logger.info("§3Loading mobs config...")
            val configContainerInstance = ConfigLoader.loadConfig<MobsConfigContainer>("mobs/mobs")
            if(configContainerInstance == null) {
                NotTowerDefense.instance.logger.severe("§4Config cannot be loaded")
            } else {
                NotTowerDefense.instance.logger.info("§bLoaded mobs config.")
                configContainerInstance.mobs?.forEach { it.check() }
                instance = configContainerInstance
            }
        }
    }
}
