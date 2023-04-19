package fr.not_here.not_tower_defense.config.models

import org.bukkit.entity.EntityType

data class GameMobConfig(
    var name: String = "zombie",
    var displayName: String = "§cZombie",
    var entityType: String = EntityType.ZOMBIE.name,
    var health: Double = 10.0,
    var speed: Double = 0.2,
    var damage: Double = 1.0,
    var reward: Int = 10,
) {
    val entityTypeEnum: EntityType get() = EntityType.valueOf(entityType)

    fun check() {
        if(entityType !in EntityType.values().map { it.name }) {
            throw IllegalArgumentException("Invalid entityType $entityType, on config path: game.mobs[?].entityType, for mob $name")
        }
    }
}
