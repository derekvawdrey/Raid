package me.amogus360.raid.Commands.Faction;

import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DAO.FactionDao;
import me.amogus360.raid.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class ClaimLandCommand extends RaidCommand {
    private final FactionDao factionDao;

    public ClaimLandCommand(JavaPlugin plugin, String usage, FactionDao factionDataAccess) {
        super(plugin, usage);
        this.factionDao = factionDataAccess;
    }
    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        // Check if the player is in a faction and is the owner or has a specific title (e.g., "officer")
        if (!factionDao.isPlayerInFaction(playerUUID)) {
            player.sendMessage("You are not currently in a faction.");
            return;
        }

        if (!factionDao.isPlayerFactionOwner(playerUUID) && !factionDao.hasTitle(playerUUID, "officer")) {
            player.sendMessage("Only the faction owner or officers can claim land.");
            return;
        }
        int factionId = factionDao.getFactionIdByPlayerUUID(player.getUniqueId());

        // Get the location where the player wants to claim land (you need to implement this part)

        // Check for overlap with other factions' claims within 64 blocks (you need to implement this part)
        if (factionDao.hasOverlapWithOtherFactions(factionId,player.getLocation(),16)) {
            player.sendMessage("Land claim failed due to overlap with other factions.");
            return;
        }

        if (!factionDao.isWithinRadiusOfSameFactionClaim(factionId,player.getLocation(),8)) {
            player.sendMessage("Land claim failed due to insufficient proximity to an existing claim.");
            return;
        }

        // If all conditions are met, execute the land claim by adding it to the database (you need to implement this part)
        factionDao.claimLand(factionId,player.getLocation());
        player.sendMessage("Land claimed successfully.");
    }

    private void executeLandClaim(Location location) {
        // Add the new land claim to the database
        // Implement this logic to insert the claim into your database
    }
}
