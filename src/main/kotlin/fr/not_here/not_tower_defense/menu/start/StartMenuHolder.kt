package fr.not_here.not_tower_defense.menu.start

import fr.not_here.not_tower_defense.NotTowerDefense
import fr.not_here.not_tower_defense.config.containers.GamesConfigContainer
import fr.not_here.not_tower_defense.config.containers.GlobalConfigContainer
import fr.not_here.not_tower_defense.config.containers.PowersConfigContainer
import fr.not_here.not_tower_defense.config.models.GamePowerConfig
import fr.not_here.not_tower_defense.extensions.addAll
import fr.not_here.not_tower_defense.extensions.glowing
import fr.not_here.not_tower_defense.managers.GameManager
import fr.not_here.not_tower_defense.managers.Message
import fr.not_here.not_tower_defense.menu.CustomMenuHolder
import fr.not_here.not_tower_defense.menu.MenuItem
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.UUID
import kotlin.math.min

class StartMenuHolder(val player: Player): CustomMenuHolder() {
  override fun getInventory() = inv

  private val inv: Inventory = Bukkit.createInventory(this, 6*9, "Start Menu")
  private var playerPage = 0
  private val powers = mutableListOf<GamePowerConfig>().apply { addAll(PowersConfigContainer.instance!!.powers!!) }
  private var powerNameSelected = PowersConfigContainer.instance!!.powers!!.first().name
  private val playersSelected = mutableListOf<UUID>()
  private val players get() = playersSelected.mapNotNull { Bukkit.getPlayer(it) }

  val powerSelected: GamePowerConfig
    get() = powers.first { it.name == powerNameSelected }

  val canGoNextPage: Boolean
    get() = playerPage < (Bukkit.getOnlinePlayers().size - 1) / 14

  val canGoPreviousPage: Boolean
    get() = playerPage > 0

  override fun onClick(slot: Int, item: ItemStack?){}

  override fun onOpen(){
    fill(MenuItem(Material.STAINED_GLASS_PANE, 1, 15.toShort()))
    fill(MenuItem(Material.AIR), 2, 2, 7, 2)
    updatePlayers()
    updatePowers()
    setItem(MenuItem(Material.WOOL, 1, 5.toShort(), "Démarrer").apply { onClick = { startGame() } }, 5, 6)
  }

  fun startGame(){
    val askedPlayers = players
    for (askedPlayer in askedPlayers) {
      askedPlayer.sendMessage(Message.gameAsked("player" to player.name))
      askedPlayer.spigot().sendMessage(
        TextComponent(Message.accept())
          .apply {
            clickEvent = ClickEvent(
              ClickEvent.Action.RUN_COMMAND,
              "/td join ${player.name}"
            )
          }
      )
    }
    GameManager.startGame(player, powerSelected, true, askedPlayers)
    player.closeInventory()
  }

  fun selectPlayer(player: Player){
    if(playersSelected.contains(player.uniqueId)) playersSelected.remove(player.uniqueId)
    else playersSelected.add(player.uniqueId)
    updatePlayers()
  }

  private fun updatePlayers(){
    val players = player.location.world.players.filter { it != player && !GameManager.games.map { g -> g.players }.flatten().contains(it) }
    val items = players.map {
      if(playersSelected.contains(it.uniqueId))
        MenuItem(Material.BARRIER, name = it.displayName).apply{ onClick = { selectPlayer(it) } }
      else MenuItem(it, name = it.displayName).apply{ onClick = { selectPlayer(it) } }
    }
    fill(items.subList(min(playerPage * 14, items.size), min(playerPage * 14 + 14, items.size)), 2, 2, 7, 2)

    setItem(MenuItem(if(!canGoPreviousPage) Material.BARRIER else Material.ARROW, name = GlobalConfigContainer.instance!!.previous).apply { onClick = {
      if(canGoPreviousPage) { playerPage--; updatePlayers() }
    } }, 3, 4)
    setItem(MenuItem(if(!canGoNextPage) Material.BARRIER else Material.ARROW, name = GlobalConfigContainer.instance!!.next).apply { onClick = {
      if(canGoNextPage) { playerPage++; updatePlayers() }
    } }, 7, 4)
  }

  private fun updatePowers(){
    val items = powers.map { MenuItem(it.displayItemEnum, name=it.displayName).apply {
      lore = mutableListOf(GlobalConfigContainer.instance!!.costPattern.replace("{cost}", it.cost.toString())).apply { addAll(it.description) }
      onClick = { powerNameSelected = it.name; updatePowers() } }
    }
    items[powers.indexOf(powerSelected)].glow
    fill(items, 2 + (3 - items.size/2), 5, items.size, 1)
  }
}