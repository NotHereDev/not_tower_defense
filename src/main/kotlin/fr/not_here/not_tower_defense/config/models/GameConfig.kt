package fr.not_here.not_tower_defense.config.models

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.classes.Position
import fr.not_here.not_tower_defense.classes.Zone
import fr.not_here.not_tower_defense.config.containers.MobsConfigContainer
import fr.not_here.not_tower_defense.config.containers.PowersConfigContainer
import fr.not_here.not_tower_defense.config.containers.TowersConfigContainer
import org.bukkit.Material
import java.time.OffsetDateTime


data class GameConfig(
    var name: String = "game",
    var displayName: String? = "§6Game",
    var health: Double = 100.0,
    var waitingRoom: Zone = Zone(Position(0.0, 0.0, 0.0), Position(0.0, 0.0, 0.0)),
    var waitingRoomSpawn: Position? = null,
    var gameRoom: Zone = Zone(Position(0.0, 0.0, 0.0), Position(0.0, 0.0, 0.0)),
    var gameRoomSpawn: Position? = null,
    var startRoom: Zone = Zone(Position(0.0, 0.0, 0.0), Position(0.0, 0.0, 0.0)),
    var endRoom: Zone = Zone(Position(0.0, 0.0, 0.0), Position(0.0, 0.0, 0.0)),
    var pathSteps: List<Zone> = listOf(),
    var towerNames: List<String> = listOf(),
    var towerOnBlockType: String = Material.GOLD_BLOCK.name,
    var waveChangeTimeout: Int = 100,
    var waves: List<GameWaveConfig> = listOf(),
    var startingMoney: Int = 100,
    var towerSellPercentage: Double = 0.75,
    var powers: List<String> = listOf(),
    var arenaOffset: Position = Position(0.0, 0.0, 0.0),
    var maxArenaCount: Int = 20,
    var runCommandsOnWinForEachPlayers: List<String> = listOf(),
    var sendMessagesOnWinForEachPlayers: List<String> = listOf(),
    var runCommandsOnLooseForEachPlayers: List<String> = listOf(),
    var sendMessagesOnLooseForEachPlayers: List<String> = listOf(),
) {

    val towers: List<GameTowerConfig> get() = TowersConfigContainer.instance!!.towers!!.filter { it.name in towerNames }

    val towerOnBlockMaterial: Material get() = Material.getMaterial(towerOnBlockType)

    fun check() {
        if (waitingRoomSpawn == null) {
            waitingRoomSpawn = Position(waitingRoom.center.x, waitingRoom.center.y, waitingRoom.center.z)
        }
        if (gameRoomSpawn == null) {
            gameRoomSpawn = Position(gameRoom.center.x, gameRoom.center.y, gameRoom.center.z)
        }
        if (towerOnBlockType !in Material.values().map { it.name }) {
            NotTowerDefense.instance.logger.severe("§4Invalid towerOnBlockType: $towerOnBlockType, on config path: game.$name")
        }
        val mobNames = MobsConfigContainer.instance!!.mobs!!.map { it.name }
        for ((i, wave) in waves.withIndex()) {
            for ((j, step) in wave.steps.withIndex()) {
                if (step.mobName !in mobNames) {
                    NotTowerDefense.instance.logger.severe("§4Mob ${step.mobName} is not defined in mobs list (mobs[?].name) on config path: game.$name.waves[$i].steps[$j].mobName, valid mobs are: $mobNames")
                }
            }
        }
        if(towerNames.isEmpty()) {
            NotTowerDefense.instance.logger.severe("§4No tower defined in game.$name.towerNames")
        }
        val configTowerNames = TowersConfigContainer.instance!!.towers!!.map { it.name }
        towerNames.forEach { towerName ->
            if(towerName !in configTowerNames) {
                NotTowerDefense.instance.logger.severe("§4Tower $towerName is not defined in towers list (towers[?].name) on config path: game.$name.towerNames[?], valid towers are: $configTowerNames")
            }
        }
        if(powers.isEmpty()) {
            NotTowerDefense.instance.logger.severe("§4No power defined in game.$name.powers")
        }
        val configPowerNames = PowersConfigContainer.instance!!.powers!!.map { it.name }
        powers.forEach { powerName ->
            if(powerName !in configPowerNames) {
                NotTowerDefense.instance.logger.severe("§4Power $powerName is not defined in powers list (powers[?].name) on config path: game.$name.powers[?], valid powers are: $configPowerNames")
            }
        }
        if (towerSellPercentage < 0.0 || towerSellPercentage > 1.0) {
            NotTowerDefense.instance.logger.severe("§4Invalid towerSellPercentage: $towerSellPercentage, on config path: game.$name, must be between 0.0 and 1.0")
        }
    }

    fun applyOffset(offset: Position) {
        waitingRoom = waitingRoom.copy() + offset
        waitingRoomSpawn = waitingRoomSpawn!!.copy() + offset
        gameRoom = gameRoom.copy() + offset
        gameRoomSpawn = gameRoomSpawn!!.copy() + offset
        startRoom = startRoom.copy() + offset
        endRoom = endRoom.copy() + offset
        pathSteps = pathSteps.map { it.copy() + offset }
    }
}