package fr.not_here.not_tower_defense.menu.start

import fr.not_here.not_tower_defense.config.containers.GamesConfigContainer
import fr.not_here.not_tower_defense.extensions.addAll
import fr.not_here.not_tower_defense.extensions.fill
import fr.not_here.not_tower_defense.extensions.glowing
import fr.not_here.not_tower_defense.extensions.head
import fr.not_here.not_tower_defense.managers.GameManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.UUID

class StartMenuHolder(val player: Player): CustomMenuHolder {
  override fun getInventory() = inv

  private val inv: Inventory = Bukkit.createInventory(this, 6*9, "Start Menu")
  private var playerPage = 0
  private val powers = listOf("Fire", "Water", "Earth", "Air", "Light", "Dark", "Ether")
  private var powerNameSelected = ""
  private val playersSelected = mutableMapOf<String, UUID>()

  override fun onEvent(event: InventoryEvent){
    when(event){
      is InventoryDragEvent -> {
        event.isCancelled = true
      }
      is InventoryClickEvent -> {
        event.isCancelled = true
        onClick(event.slot, event.currentItem)
      }
      is InventoryOpenEvent -> onOpen()
    }
  }

  private fun onClick(slot: Int, item: ItemStack?){
    if(item == null) return
    when(slot){
      in 9*1+1..9*1+7 -> {selectPlayer(item)}
      in 9*2+1..9*2+7 -> {selectPlayer(item)}
      9*3+2 -> {
        if (playerPage == 0) return
        playerPage--
        updatePlayers()
      }
      9*3+6 -> {
        if(playerPage * 14 + 14 >= player.location.world.players.size) return
        playerPage++
        updatePlayers()
      }
      in 9*4+1..9*4+7 -> {
        powerNameSelected = powers[slot - 9*4 - 1]
        updatePowers()
      }
      9*5+4 -> {
        if(powerNameSelected == "") return
        val name = GamesConfigContainer.instance?.games?.firstOrNull()?.name ?: return

        GameManager.startOrRestartGame(name, player, true)
      }
    }
  }

  fun selectPlayer(item: ItemStack){
    if(item.type != Material.SKULL_ITEM && item.itemMeta !is SkullMeta) {
      if(playersSelected.contains(player.displayName)) {
        playersSelected.remove(player.displayName)
      }
    } else {
      val skullOwner = (item.itemMeta as SkullMeta).owningPlayer
      val player = Bukkit.getPlayer(skullOwner.uniqueId)
      playersSelected[player.displayName] = player.uniqueId
    }
    updatePlayers()
  }

  private fun onOpen(){
    inv.fill(ItemStack(Material.STAINED_GLASS_PANE, 1, 15.toShort()))
    //we want to empty the inventory where player heads will appear
    inv.addAll(listOf(ItemStack(Material.AIR), ItemStack(Material.AIR), ItemStack(Material.AIR), ItemStack(Material.AIR), ItemStack(Material.AIR), ItemStack(Material.AIR), ItemStack(Material.AIR)), 9*1+1)
    inv.addAll(listOf(ItemStack(Material.AIR), ItemStack(Material.AIR), ItemStack(Material.AIR), ItemStack(Material.AIR), ItemStack(Material.AIR), ItemStack(Material.AIR), ItemStack(Material.AIR)), 9*2+1)
    updatePlayers()
    updatePowers()
    inv.setItem(9*5+4, ItemStack(Material.WOOL, 1, 5.toShort()).apply { itemMeta = itemMeta.apply { displayName = "Start" } })
  }

  private fun updatePlayers(){
    val players = player.location.world.players.filter { it != player && GameManager.games.map { it.players }.flatten().contains(it) }
    val items = players.map {
      if(playersSelected.containsValue(it.uniqueId))
        ItemStack(Material.BARRIER).apply { itemMeta = itemMeta.apply { displayName = it.displayName } }
      else it.head
    }
    if(items.size >= playerPage * 14 + 1)
      inv.addAll(items.subList(0 + playerPage * 14, (7 + playerPage * 14).coerceAtMost(players.size)), 9*1+1)
    if(items.size >= playerPage * 14 + 8)
      inv.addAll(items.subList(7 + playerPage * 14, (14 + playerPage * 14).coerceAtMost(players.size)), 9*2+1)

    val canGoBack = playerPage > 0
    inv.setItem(9*3+2, ItemStack(if(!canGoBack) Material.BARRIER else Material.ARROW, 1).apply { itemMeta = itemMeta.apply { displayName = "Précédent" } })
    val canGoForward = players.size > playerPage * 14 + 14
    inv.setItem(9*3+6, ItemStack(if(!canGoBack) Material.BARRIER else Material.ARROW, 1).apply { itemMeta = itemMeta.apply { displayName = "Suivant" } })
  }

  private fun updatePowers(){
    val items = powers.map { ItemStack(Material.WOOL, 1, 0.toShort()).apply { itemMeta = itemMeta.apply { displayName = it } } }
    if(powerNameSelected.isEmpty()) powerNameSelected = powers[0]
    items[powers.indexOf(powerNameSelected)].glowing
    inv.addAll(items, 9*4+1)
  }

}