package me.amogus360.raid.EventHandlers;

import me.amogus360.raid.TableManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinEventHandler implements Listener {

    private final TableManager tableManager; // Assuming you have a TableManager instance

    public PlayerJoinEventHandler(TableManager tableManager) {
        this.tableManager = tableManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Get the player who joined
        Player player = event.getPlayer();

        // Get the player's name and UUID
        String playerName = player.getName();
        UUID playerUUID = player.getUniqueId();

        // Insert the player's username and UUID into the database
        tableManager.insertPlayer(playerUUID, playerName);

        // Perform any other actions you want
        // For example, send a welcome message to the player
        player.sendMessage("Welcome to the server, " + playerName + "!");
    }
}
