package me.amogus360.raid.Commands;
import me.amogus360.raid.RaidCommandManager;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class AddMoneyCommand extends RaidCommand {

    public AddMoneyCommand(JavaPlugin plugin) {
        super(plugin);
    }
    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        if (args.length != 3) {
            sender.sendMessage("Usage: /raid addmoney <player> <amount>");
            return;
        }

        String playerName = args[1];
        int amount;

        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid amount. Please enter a valid number.");
            return;
        }

        Player targetPlayer = plugin.getServer().getPlayer(playerName);
        UUID playerUUID = targetPlayer.getUniqueId();



    }
}
