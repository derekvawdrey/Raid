package me.amogus360.raid.Commands.Faction;

import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DAO.FactionDao;
import me.amogus360.raid.DAO.PlayerAccountDao;
import me.amogus360.raid.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class DepositFactionCommand extends RaidCommand {
    private final FactionDao factionDao;
    private final PlayerAccountDao playerDao;

    public DepositFactionCommand(JavaPlugin plugin, String usage, FactionDao factionDataAccess, PlayerAccountDao playerDao) {
        super(plugin, usage);
        this.factionDao = factionDataAccess;
        this.playerDao = playerDao;
    }

    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        // Check if the player is in a faction
        if (!factionDao.isPlayerInFaction(playerUUID)) {
            player.sendMessage("You are not currently in a faction.");
            return;
        }

        // Check if the player is the owner of the faction
        if (!factionDao.isPlayerFactionOwner(playerUUID)) {
            player.sendMessage("Only the faction owner can deposit money.");
            return;
        }

        if (args.length != 1) {
            player.sendMessage("Usage: /depositmoney <amount>");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("Please enter a valid amount.");
            return;
        }

        // Check if the amount is positive
        if (amount <= 0) {
            player.sendMessage("Please enter a positive amount.");
            return;
        }

        try {
            int playerBalance = this.playerDao.getBalance(playerUUID);
            if (playerBalance < amount) {
                player.sendMessage("You do not have enough money to deposit.");
                return;
            }
        }
        catch (Exception e){
            return;
        }

        String factionName = factionDao.getFactionNameByPlayerUUID(playerUUID);

        // Deposit money into the faction bank
        factionDao.depositMoneyToFaction(playerUUID, factionName, amount);
        player.sendMessage("You have deposited " + amount + " money into your faction's bank.");
    }
}
