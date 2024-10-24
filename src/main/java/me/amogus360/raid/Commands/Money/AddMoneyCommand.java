package me.amogus360.raid.Commands.Money;
import me.amogus360.raid.CommandManager.CommandManager;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.CommandManager.RaidCommandManager;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class AddMoneyCommand extends RaidCommand {

    public AddMoneyCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }
    @Override
    public void execute(CommandSender sender, String[] args, CommandManager commandManager) {
        if (args.length != 2) {
            this.tellUsage(sender);
            return;
        }
        DataAccessManager dataAccessManager = commandManager.getDataAccessManager();

        String playerName = args[0];
        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            MessageManager.sendMessage(sender,"Invalid amount. Please enter a valid number.");
            return;
        }

        Player targetPlayer = plugin.getServer().getPlayer(playerName);
        UUID playerUUID = targetPlayer.getUniqueId();
        try {
            dataAccessManager.getPlayerAccountDao().updateBalance(playerUUID,amount);
        } catch(Exception e) {

        }



    }
}
