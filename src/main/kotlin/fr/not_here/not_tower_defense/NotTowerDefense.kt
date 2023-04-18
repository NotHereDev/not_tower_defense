package fr.not_here.not_tower_defense

import fr.not_here.not_tower_defense.commands.RunCommand
import fr.not_here.not_tower_defense.commands.StopCommand
import fr.not_here.not_tower_defense.listeners.EntityCombustListener
import fr.not_here.not_tower_defense.listeners.EntityDamageListener
import fr.not_here.not_tower_defense.listeners.TowerInteractListener
import org.bukkit.plugin.java.JavaPlugin

class NotTowerDefense : JavaPlugin() {
    override fun onEnable() {
        instance = this
        // Plugin startup logic
        getCommand("run")?.setExecutor(RunCommand())
        getCommand("stop")?.setExecutor(StopCommand())

        server.pluginManager.registerEvents(TowerInteractListener(), this)
        server.pluginManager.registerEvents(EntityCombustListener(), this)
        server.pluginManager.registerEvents(EntityDamageListener(), this)
    }

    override fun onDisable() {
        RunCommand.gameInstances.forEach { it.stop() }
        // Plugin shutdown logic
    }

    companion object {
        lateinit var instance: NotTowerDefense
            private set
    }
}
