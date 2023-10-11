package me.amogus360.raid.Commands.Faction;

import me.amogus360.raid.CommandManager.CommandManager;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.CommandManager.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class DepositFactionCommand extends RaidCommand {

    public DepositFactionCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }

    @Override
    public void execute(CommandSender sender, String[] args, CommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender,"Only players can use this command.");
            return;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        DataAccessManager dataAccessManager = commandManager.getDataAccessManager();
        // Check if the player is in a faction
        if (!dataAccessManager.getFactionDao().isPlayerInFaction(playerUUID)) {
            MessageManager.sendMessage(player,"You are not currently in a faction.");
            return;
        }

        // Check if the player is the owner of the faction
        if (!dataAccessManager.getFactionDao().isPlayerFactionOwner(playerUUID)) {
            MessageManager.sendMessage(player,"Only the faction owner can deposit money.");
            return;
        }

        if (args.length != 1) {
            MessageManager.sendMessage(player,"Usage: /depositmoney <amount>");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            MessageManager.sendMessage(player,"Please enter a valid amount.");
            return;
        }

        // Check if the amount is positive
        if (amount <= 0) {
            MessageManager.sendMessage(player,"Please enter a positive amount.");
            return;
        }

        try {
            int playerBalance = dataAccessManager.getPlayerAccountDao().getBalance(playerUUID);
            if (playerBalance < amount) {
                MessageManager.sendMessage(player,"You do not have enough money to deposit.");
                return;
            }
        }
        catch (Exception e){
            return;
        }

        String factionName = dataAccessManager.getFactionDao().getFactionNameByPlayerUUID(playerUUID);

        // Deposit money into the faction bank
        dataAccessManager.getFactionDao().depositMoneyToFaction(playerUUID, factionName, amount);
        MessageManager.sendMessage(player,"You have deposited " + amount + " money into your faction's bank.");
    }
}
