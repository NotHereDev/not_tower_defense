package fr.not_here.not_tower_defense.config.models

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.enums.MobAttack

data class MobAttackConfig(
  var name: String = MobAttack.DOWNGRADE_TOWER.name,
  var delay: Int = 200,
  var duration: Double = 100.0,
  var power: Double = 1.0,
  var range: Double = 5.0,
){
  val attackEnum: MobAttack get() = MobAttack.valueOf(name)

  fun check() {
    if(name !in MobAttack.values().map { it.name }) {
      NotTowerDefense.instance.logger.severe("§4Invalid attack, on config path: attacks[?].name, for attack $name, valid values are: ${MobAttack.values().map { it.name }}")
    }
  }
}
