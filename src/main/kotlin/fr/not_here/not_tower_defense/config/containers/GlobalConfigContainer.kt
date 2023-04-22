package fr.not_here.not_tower_defense.config.containers

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.ConfigLoader
import org.bukkit.Material

data class GlobalConfigContainer(
  var waveEntityNamePattern: String = "{mobName} §r§8[§3{health}§f/§3{maxHealth}§8]",
  var towerLevelDisplayItems: List<String> = listOf(Material.COBBLESTONE.name, Material.STONE.name, Material.COAL_BLOCK.name, Material.IRON_BLOCK.name, Material.GOLD_BLOCK.name, Material.DIAMOND_BLOCK.name, Material.EMERALD_BLOCK.name),
) {

  fun check(){
    for(towerLevelDisplayItem in towerLevelDisplayItems) {
      if (Material.getMaterial(towerLevelDisplayItem) == null) {
        NotTowerDefense.instance.logger.severe("§4Invalid material name $towerLevelDisplayItem in global config towerLevelDisplayItems")
      }
    }
  }

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
        configContainerInstance.check()
        NotTowerDefense.instance.logger.info("§bLoaded global config.")
        instance = configContainerInstance
      }
    }
  }
}
