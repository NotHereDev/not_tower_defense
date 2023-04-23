package fr.not_here.not_tower_defense

import fr.not_here.not_tower_defense.commands.TdCommand
import fr.not_here.not_tower_defense.config.ConfigLoader
import fr.not_here.not_tower_defense.listeners.*
import fr.not_here.not_tower_defense.managers.GameManager
import org.bukkit.plugin.java.JavaPlugin

class NotTowerDefense : JavaPlugin() {
    override fun onEnable() {
        instance = this
        // Plugin startup logic
        getCommand("td")?.setExecutor(TdCommand())

        server.pluginManager.registerEvents(TowerInteractListener(), this)
        server.pluginManager.registerEvents(EntityCombustListener(), this)
        server.pluginManager.registerEvents(EntityDamageListener(), this)
        server.pluginManager.registerEvents(InventoryListener(), this)
        server.pluginManager.registerEvents(PlayerAuthListener(), this)
        server.pluginManager.registerEvents(EntityTargetListener(), this)

        ConfigLoader.init()
    }

    override fun onDisable() {
        GameManager.games.forEach { it.stop() }
        // Plugin shutdown logic
    }

    companion object {
        lateinit var instance: NotTowerDefense
            private set
    }
}
