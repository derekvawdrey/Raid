package me.amogus360.raid.Commands;

import me.amogus360.raid.TableManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MoneyCommand implements CommandExecutor {

    private final TableManager tableManager;

    public MoneyCommand(TableManager tableManager) {
        this.tableManager = tableManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can check their money.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        // Check if the player exists in the database
        if (!tableManager.playerExists(playerUUID)) {
            player.sendMessage("You are not registered in the database. Please contact an admin.");
            return true;
        }

        // Retrieve the player's money from the database
        int money = tableManager.getPlayerMoney(playerUUID);

        player.sendMessage("Your current money: " + money);
        return true;
    }
}
