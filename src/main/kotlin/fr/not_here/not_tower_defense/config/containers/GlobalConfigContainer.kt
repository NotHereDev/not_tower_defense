package fr.not_here.not_tower_defense.config.containers

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.ConfigLoader

data class GlobalConfigContainer(
  var waveEntityNamePattern: String = "{mobName} §r§8[§3{health}§f/§3{maxHealth}§8]",
) {
  companion object {
    @JvmStatic
    var instance: GlobalConfigContainer? = null
      private set

    fun load() {
      instance = null
      NotTowerDefense.instance.logger.info("§3Loading global config...")
      val configContainerInstance = ConfigLoader.loadConfig<GlobalConfigContainer>("global")
      if(configContainerInstance == null) {
        NotTowerDefense.instance.logger.severe("§4Config cannot be loaded")
      } else {
        NotTowerDefense.instance.logger.info("§bLoaded global config.")
        instance = configContainerInstance
      }
    }
  }
}
