package me.amogus360.raid.Commands.NoCommandManager.TeleportRequest;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.Items.Misc.TeleportRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeleportDeny implements CommandExecutor {

    private final JavaPlugin plugin;
    private final DataAccessManager dataAccessManager;

    public TeleportDeny(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        this.plugin = plugin;
        this.dataAccessManager = dataAccessManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            MessageManager.sendMessage(commandSender, "Only players can use this command.");
            return false;
        }

        Player player = (Player) commandSender;

        if (!player.hasPermission("factionsrevived.tpdeny")) {
            MessageManager.sendMessage(player, "You don't have permission to run this command");
            return true;
        }

        // Check if the correct number of arguments is provided
        if (strings.length != 1) {
            MessageManager.sendMessage(player, "Usage: /tpdeny <activatorName>");
            return true;
        }

        // Parse the activator name from the command arguments
        String activatorName = strings[0];
        TeleportRequest teleportRequest = dataAccessManager.getItemDao().getTeleportRequest(activatorName);

        if (teleportRequest != null) {
            // Handle teleportation response for the specific request
            teleportRequest.handleTeleportationResponse(false, dataAccessManager);
        } else {
            MessageManager.sendMessage(player, "There is no pending teleportation request from that player.");
        }

        return true;
    }
}
