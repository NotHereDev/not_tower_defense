package fr.not_here.not_tower_defense.config.containers

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.ConfigLoader
import org.bukkit.Material
import org.bukkit.boss.BarColor

data class GlobalConfigContainer(
  var waveEntityNamePattern: String = "{mobName} §r§8[§3{health}§f/§3{maxHealth}§8]",
  var towerLevelDisplayItems: List<String> = listOf(Material.COBBLESTONE.name, Material.STONE.name, Material.COAL_BLOCK.name, Material.IRON_BLOCK.name, Material.GOLD_BLOCK.name, Material.DIAMOND_BLOCK.name, Material.EMERALD_BLOCK.name),

  var waveBossBarColor: String = BarColor.BLUE.name,
  var waveBossBarTitle: String = "§a§lWave {waveNumber}/{totalWaveNumber} §r| §6§lMoney: {money} §r| §b§lProgress: §a{waveProgress}% | §4§lMobs left: {mobsLeft}",

  var healthBossBarColor: String = BarColor.RED.name,
  var healthBossBarTitle: String = "§c§lHealth: §4{health}§c/§4{maxHealth}",

  var costPattern: String = "§6§lCost: §e{cost}",
  var levePattern: String = "§6§lLevel: §e{level}",
  var sellPattern: String = "§6§lSell: §e{sell}",

  var next: String = "§a§lNext",
  var previous: String = "§c§lPrevious",

  var winTitle: String = "§a§lYou win!",
  var winSubtitle: String = "§a§lYou have won the game!",

  var looseTitle: String = "§c§lYou loose!",
  var looseSubtitle: String = "§c§lYou have lost the game at wave {waveNumber}!",

  var worldGameConfig: Map<String, String> = mapOf("world" to "game"),

  var aimClosestDisplayText: String = "§a§lAim closest",
  var aimClosestDisplayItem: String = Material.FISHING_ROD.name,
  var aimStrongestDisplayText: String = "§a§lAim strongest",
  var aimStrongestDisplayItem: String = Material.DIAMOND_SWORD.name,
  var sellDisplayText: String = "§a§lSell",
  var sellDisplayItem: String = Material.GOLD_INGOT.name,
) {

  val waveBossBarColorEnum: BarColor
    get() = BarColor.valueOf(waveBossBarColor)

  val healthBossBarColorEnum: BarColor
    get() = BarColor.valueOf(healthBossBarColor)

  val aimClosestDisplayEnum: Material
    get() = Material.getMaterial(aimClosestDisplayItem)!!

  val aimStrongestDisplayEnum: Material
    get() = Material.getMaterial(aimStrongestDisplayItem)!!

  val sellDisplayEnum: Material
    get() = Material.getMaterial(sellDisplayItem)!!

  fun check(){
    for(towerLevelDisplayItem in towerLevelDisplayItems) {
      if (Material.getMaterial(towerLevelDisplayItem) == null) {
        NotTowerDefense.instance.logger.severe("§4Invalid material name $towerLevelDisplayItem in global config towerLevelDisplayItems, valid values are: ${Material.values().map { it.name }}")
      }
    }
    if(waveBossBarColor !in BarColor.values().map { it.name }) {
      NotTowerDefense.instance.logger.severe("§4Invalid bar color name $waveBossBarColor in global config waveBossBarColor, valid values are: ${BarColor.values().map { it.name }}")
    }
    if(healthBossBarColor !in BarColor.values().map { it.name }) {
      NotTowerDefense.instance.logger.severe("§4Invalid bar color name $healthBossBarColor in global config healthBossBarColor, valid values are: ${BarColor.values().map { it.name }}")
    }
    val gameNames = GamesConfigContainer.instance!!.games!!.map { it.name }
    for((_, game) in worldGameConfig) {
      if(game !in gameNames) {
        NotTowerDefense.instance.logger.severe("§4Invalid game name $game in global config worldGameConfig, valid values are: $gameNames")
      }
    }
    if(Material.getMaterial(aimClosestDisplayItem) == null) {
      NotTowerDefense.instance.logger.severe("§4Invalid material name $aimClosestDisplayItem in global config aimClosestDisplayItem, valid values are: ${Material.values().map { it.name }}")
    }
    if(Material.getMaterial(aimStrongestDisplayItem) == null) {
      NotTowerDefense.instance.logger.severe("§4Invalid material name $aimStrongestDisplayItem in global config aimStrongestDisplayItem, valid values are: ${Material.values().map { it.name }}")
    }
    if(Material.getMaterial(sellDisplayItem) == null) {
      NotTowerDefense.instance.logger.severe("§4Invalid material name $sellDisplayItem in global config sellDisplayItem, valid values are: ${Material.values().map { it.name }}")
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
