package me.amogus360.raid.Commands.NoCommandManager;

import me.amogus360.raid.MessageManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {

    private final JavaPlugin plugin; // You need to pass the plugin instance to the constructor.

    public SpawnCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            MessageManager.sendMessage(commandSender, "Only players can use this command.");
            return false; // Return false to indicate that the command did not execute successfully.
        }

        Player player = (Player) commandSender;

        // Load the spawn location from the plugin's configuration.
        Location location = (Location) plugin.getConfig().get("spawnLocation");

        if (location != null) {
            player.teleport(location);
            return true; // Return true to indicate a successful execution of the command.
        } else {
            MessageManager.sendMessage(commandSender, "Spawn location is not set.");
            return false;
        }
    }
}
