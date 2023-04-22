package fr.not_here.not_tower_defense.listeners

import fr.not_here.not_tower_defense.managers.GameManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerAuthListener: Listener {
  @EventHandler
  fun onLogin(event: PlayerJoinEvent){

  }
  @EventHandler
  fun onLogin(event: PlayerQuitEvent){
    GameManager.getGame(event.player, false)?.playerLeave(event.player)
  }
}