package me.amogus360.raid.Commands.NoCommandManager;

import me.amogus360.raid.MessageManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class WildTeleportCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private Location wildLocation; // Add this field to store the wild location.

    public WildTeleportCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        // Initialize the wildLocation here, or you can load it from the plugin's configuration.
        // Example: wildLocation = new Location(yourWorld, x, y, z);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            MessageManager.sendMessage(commandSender, "Only players can use this command.");
            return false;
        }

        Player player = (Player) commandSender;
        player.teleport(getRandomSafeLocation(player));
        return true;
    }
    public Location getRandomSafeLocation(Player player) {
        World world = player.getWorld();
        Random random = new Random();

        // Define the maximum number of attempts to find a safe location.
        int radius = 2500;
        double angle = random.nextDouble() * 2 * Math.PI; // Random angle in radians.
        double distance = random.nextDouble() * radius;

        double x = 0 + distance * Math.cos(angle);
        double z = 0 + distance * Math.sin(angle);
        double y = world.getHighestBlockYAt((int) x, (int) z);

        Location randomLocation = new Location(world, x, y, z);

        // Check if the location is safe (not inside a block and not too high).
        return randomLocation;
    }
}
