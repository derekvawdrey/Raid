package me.amogus360.raid.Commands.Faction;

import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DAO.FactionDao;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.LandClaim;
import me.amogus360.raid.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class ClaimLandCommand extends RaidCommand {

    public ClaimLandCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }
    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender,"Only players can use this command.");
            return;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        // Check if the player is in a faction and is the owner or has a specific title (e.g., "officer")
        if (!commandManager.getFactionDao().isPlayerInFaction(playerUUID)) {
            MessageManager.sendMessage(player,"You are not currently in a faction.");
            return;
        }

        if (!commandManager.getFactionDao().isPlayerFactionOwner(playerUUID) && !commandManager.getFactionDao().hasTitle(playerUUID, "officer")) {
            MessageManager.sendMessage(player,"Only the faction owner or officers can claim land.");
            return;
        }
        int factionId = commandManager.getFactionDao().getFactionIdByPlayerUUID(player.getUniqueId());

        // Get the location where the player wants to claim land (you need to implement this part)

        // Check for overlap with other factions' claims within 64 blocks (you need to implement this part)
        if (commandManager.getLandClaimDao().hasOverlapWithOtherFactions(factionId,player.getLocation(),4)) {
            MessageManager.sendMessage(player,"Land claim failed due to overlap with other factions.");
            return;
        }

        if (!commandManager.getLandClaimDao().isWithinRadiusOfSameFactionClaim(factionId,player.getLocation(), 1)) {
            MessageManager.sendMessage(player,"Land claim failed due to insufficient proximity to an existing claim.");
            return;
        }

        if(commandManager.getLandClaimDao().isClaimed(player.getLocation())){
            MessageManager.sendMessage(player,"You have already claimed this chunk!");
            return;
        }

        // If all conditions are met, execute the land claim by adding it to the database (you need to implement this part)
        LandClaim landClaim = commandManager.getLandClaimDao().claimLand(factionId,player.getLocation());
        MessageManager.sendMessage(player,"Land claimed successfully. Chunk_X = " + landClaim.getLocation().getX() + ", Chunk_Z = " + landClaim.getLocation().getZ());
    }
}
