package fr.not_here.not_tower_defense.config.containers

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.ConfigLoader
import fr.not_here.not_tower_defense.config.models.GameConfig

data class GamesConfigContainer(
    var games: List<GameConfig>? = null
) {

    fun check() {
        if (games == null) NotTowerDefense.instance.logger.severe("§4Games config cannot be null")
        if (games!!.isEmpty()) NotTowerDefense.instance.logger.severe("§4Games config cannot be empty")
        games?.forEach { it.check() }
    }

    companion object {
        @JvmStatic
        var instance: GamesConfigContainer? = null
            private set

        fun load() {
            instance = null
            NotTowerDefense.instance.logger.info("§3Loading games config...")
            val configContainerInstance = ConfigLoader.loadConfig<GamesConfigContainer>("games/games")
            if(configContainerInstance == null) {
                NotTowerDefense.instance.logger.severe("§4Config cannot be loaded")
            } else {
                NotTowerDefense.instance.logger.info("§bLoaded games config.")
                configContainerInstance.check()
                instance = configContainerInstance
            }
        }
    }
}
