package me.amogus360.raid.Commands;
import me.amogus360.raid.TableManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AddMoneyCommand implements CommandExecutor {

    private final TableManager tableManager;

    public AddMoneyCommand(TableManager tableManager) {
        this.tableManager = tableManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /addmoney <player> <amount>");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (targetPlayer == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid amount. Please enter a valid number.");
            return true;
        }

        if (amount < 0) {
            sender.sendMessage("Amount cannot be negative.");
            return true;
        }

        // Add the specified amount of money to the target player
        UUID targetUUID = targetPlayer.getUniqueId();
        tableManager.addMoney(targetUUID, amount);

        sender.sendMessage("Added " + amount + " money to " + targetPlayer.getName() + "'s account.");
        targetPlayer.sendMessage("You received " + amount + " money.");

        return true;
    }
}
