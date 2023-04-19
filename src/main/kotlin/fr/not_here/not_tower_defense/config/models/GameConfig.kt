package fr.not_here.not_tower_defense.config.models

import fr.not_here.not_tower_defense.classes.Position
import fr.not_here.not_tower_defense.classes.Zone
import org.bukkit.Material


data class GameConfig(
    var name: String = "game",
    var displayName: String? = "§6Game",
    var waitingRoom: Zone = Zone(Position(0.0, 0.0, 0.0), Position(0.0, 0.0, 0.0)),
    var waitingRoomSpawn: Position? = null,
    var gameRoom: Zone = Zone(Position(0.0, 0.0, 0.0), Position(0.0, 0.0, 0.0)),
    var gameRoomSpawn: Position? = null,
    var startRoom: Zone = Zone(Position(0.0, 0.0, 0.0), Position(0.0, 0.0, 0.0)),
    var endRoom: Zone = Zone(Position(0.0, 0.0, 0.0), Position(0.0, 0.0, 0.0)),
    var pathSteps: List<Zone> = listOf(),
    var towers: List<GameTowerConfig> = listOf(),
    var towerOnBlockType: String = Material.GOLD_BLOCK.name,
    var mobs: List<GameMobConfig> = listOf(),
    var waveChangeTimeout: Int = 100,
    var waves: List<GameWaveConfig> = listOf(),
) {
    val towerOnBlockMaterial: Material get() = Material.getMaterial(towerOnBlockType)

    fun check() {
        if (waitingRoomSpawn == null) {
            waitingRoomSpawn = Position(waitingRoom.center.x, waitingRoom.center.y, waitingRoom.center.z)
        }
        if (gameRoomSpawn == null) {
            gameRoomSpawn = Position(gameRoom.center.x, gameRoom.center.y, gameRoom.center.z)
        }
        if (towerOnBlockType !in Material.values().map { it.name }) {
            throw IllegalArgumentException("Invalid towerOnBlockType: $towerOnBlockType, on config path: game.$name")
        }
        val mobNames = mobs.map { it.name }
        for ((i, wave) in waves.withIndex()) {
            for ((j, step) in wave.steps.withIndex()) {
                if (step.mobName !in mobNames) {
                    throw IllegalArgumentException("Mob ${step.mobName} is not defined in mobs list (game.$name.mobs[?].name) on config path: game.$name.waves[$i].steps[$j].mobName")
                }
            }
        }
        mobs.forEach { it.check() }
        towers.forEach { it.check() }
    }
}