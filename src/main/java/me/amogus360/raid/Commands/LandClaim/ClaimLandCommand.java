package me.amogus360.raid.Commands.LandClaim;

import me.amogus360.raid.CommandManager.CommandManager;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.LandClaim.LandClaim;
import me.amogus360.raid.CommandManager.RaidCommandManager;
import me.amogus360.raid.Utilities.GeneralUtilities;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;


public class ClaimLandCommand extends RaidCommand {

    public ClaimLandCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }
    @Override
    public void execute(CommandSender sender, String[] args, CommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender,"Only players can use this command.");
            return;
        }

        int claimPrice = 50;

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        DataAccessManager dataAccessManager = commandManager.getDataAccessManager();

        // Check if the player is in a faction and is the owner or has a specific title (e.g., "officer")
        if (!dataAccessManager.getFactionDao().isPlayerInFaction(playerUUID)) {
            MessageManager.sendMessage(player,"You are not currently in a faction.");
            return;
        }

        if(GeneralUtilities.isPlayerNearSpawn(player,plugin)){
            MessageManager.sendMessage(player, "You can't claim land near spawn!");
            return;
        }

        if (!dataAccessManager.getFactionDao().isPlayerFactionOwner(playerUUID) && !dataAccessManager.getFactionDao().hasFactionTitle(playerUUID, "officer")) {
            MessageManager.sendMessage(player,"Only the faction owner or officers can claim land.");
            return;
        }
        int factionId = dataAccessManager.getFactionDao().getFactionIdByPlayerUUID(player.getUniqueId());

        // Get the location where the player wants to claim land (you need to implement this part)

        // Check for overlap with other factions' claims within 64 blocks (you need to implement this part)
        if (dataAccessManager.getLandClaimDao().hasOverlapWithOtherFactions(factionId,player.getLocation(),4)) {
            MessageManager.sendMessage(player,"Land claim failed due to overlap with other factions.");
            return;
        }

        if (!dataAccessManager.getLandClaimDao().isWithinRadiusOfSameFactionClaim(factionId,player.getLocation(), 1)) {
            MessageManager.sendMessage(player,"Land claim failed due to insufficient proximity to an existing claim.");
            return;
        }

        if(!dataAccessManager.getFactionDao().hasEnoughMoney(factionId, claimPrice)){
            MessageManager.sendMessage(player,"Your faction does not have enough money! Type /faction deposit [amount] to put money into your faction. Type /faction info to see how much you have.");
            return;
        }

        if(dataAccessManager.getLandClaimDao().isClaimed(player.getLocation())){
            MessageManager.sendMessage(player,"You have already claimed this chunk!");
            return;
        }

        // If all conditions are met, execute the land claim by adding it to the database (you need to implement this part)
        LandClaim landClaim = dataAccessManager.getLandClaimDao().claimLand(factionId,player.getLocation());
        dataAccessManager.getFactionDao().subtractMoneyFromAccount(factionId, claimPrice);
        MessageManager.sendMessage(player,"Land claimed successfully. Chunk_X = " + landClaim.getLocation().getX() + ", Chunk_Z = " + landClaim.getLocation().getZ());
    }
}
