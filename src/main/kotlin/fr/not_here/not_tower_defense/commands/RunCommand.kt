package fr.not_here.not_tower_defense.commands

import fr.not_here.not_tower_defense.classes.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

class RunCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
    if(sender !is Player) {
      sender.sendMessage("You must be a player to run this command")
      return false
    }
    var gameInstance = gameInstances.find { it.players.contains(sender) }
    if(gameInstance != null) {
      sender.sendMessage("Game already started, restarting...")
      gameInstance.stop()
      gameInstances.remove(gameInstance)
    }
    sender.sendMessage("Starting game...")
    gameInstance = Game(
      Zone(
        Position(-241.0, 92.0, 142.0),
        Position(-243.0, 93.0, 144.0)
      ),
      Zone(
        Position(-244.0, 91.0, 141.0),
        Position(-185.0, 94.0, 180.0)
      ),
      listOf(sender),
      Zone(
        Position(-229.0, 92.0, 176.0),
        Position(-231.0, 93.0, 178.0)
      ),
      Zone(
        Position(-241.0, 92.0, 142.0),
        Position(-243.0, 93.0, 144.0)
      ),
      listOf(
        Zone(
          Position(-208.0, 92.0, 178.0),
          Position(-210.0, 93.0, 180.0)
        ),
        Zone(
          Position(-188.0, 92.0, 165.0),
          Position(-190.0, 93.0, 167.0)
        ),
        Zone(
          Position(-226.0, 92.0, 160.0),
          Position(-228.0, 93.0, 162.0)
        ),
        Zone(
          Position(-210.0, 92.0, 151.0),
          Position(-212.0, 93.0, 153.0)
        ),
        Zone(
          Position(-219.0, 92.0, 142.0),
          Position(-221.0, 93.0, 144.0)
        ),
      ),
      listOf(
        listOf(
          GameWaveStep(
            GameEntityData(
              "Zombie",
              EntityType.ZOMBIE,
              20.0,
              0.01,
              1.0,
              0.0
            ),
            20,
            2,
            10
          ),
          GameWaveStep(
            GameEntityData(
              "Zombie WESHE CHUI BIZARRE",
              EntityType.CREEPER,
              20.0,
              0.5,
              1.0,
              0.0
            ),
            20,
            5,
            8
          ),
        ),
        listOf(
          GameWaveStep(
            GameEntityData(
              "Zombie",
              EntityType.ZOMBIE,
              20.0,
              0.5,
              1.0,
              0.0
            ),
            20,
            2,
            10
          ),
          GameWaveStep(
            GameEntityData(
              "Zombie WESHE CHUI BIZARRE",
              EntityType.CREEPER,
              20.0,
              0.5,
              1.0,
              0.0
            ),
            20,
            5,
            2
          ),
        )
      )
    )
    gameInstance.run()
    gameInstances.add(gameInstance)
    return true
  }

  companion object{
    var gameInstances: MutableList<Game> = mutableListOf()
  }
}