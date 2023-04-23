package fr.not_here.not_tower_defense.config.models

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.enums.MobAttack
import org.bukkit.entity.EntityType

data class GameMobConfig(
    var name: String = "zombie",
    var displayName: String = "§cZombie",
    var entityType: String = EntityType.ZOMBIE.name,
    var health: Double = 10.0,
    var speed: Double = 0.2,
    var damage: Double = 1.0,
    var reward: Int = 10,
    var dodgeChance: Double = 0.0,
    var attacks: List<MobAttackConfig> = listOf(),
    var towerAbilityImunity: Boolean = false,
) {
    val entityTypeEnum: EntityType get() = EntityType.valueOf(entityType)

    fun check() {
        if(entityType !in EntityType.values().map { it.name }) {
            NotTowerDefense.instance.logger.severe("§4Invalid entityType $entityType, on config path: mobs[?].entityType, for mob $name, valid values are: ${EntityType.values().map { it.name }}")
        }
        if (dodgeChance < 0.0 || dodgeChance > 1.0) {
            NotTowerDefense.instance.logger.severe("§4Invalid dodgeChance $dodgeChance, on config path: mobs[?].dodgeChance, for mob $name, must be between 0.0 and 1.0")
        }
        attacks.forEach { it.check() }
    }
}
