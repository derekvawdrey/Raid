package me.amogus360.raid.Utilities;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GeneralUtilities {

    public static boolean isPlayerNearSpawn(Player player, JavaPlugin plugin){

            Location playerLocation = player.getLocation();
            double distance = playerLocation.distance((Location)plugin.getConfig().get("spawnLocation"));
            return distance <= 300;

    }
}
