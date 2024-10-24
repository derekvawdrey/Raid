package me.amogus360.raid.Commands.LandClaim;

import me.amogus360.raid.CommandManager.CommandManager;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.LandClaim.LandClaim;
import me.amogus360.raid.Model.LandClaim.LandClaimArea;
import me.amogus360.raid.Model.LandClaim.LandClaimLocation;
import me.amogus360.raid.CommandManager.RaidCommandManager;
import me.amogus360.raid.Utilities.LandClaimChunkUtilities;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ShowLandClaimsCommand extends RaidCommand {
    public ShowLandClaimsCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }
    @Override
    public void execute(CommandSender sender, String[] args, CommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender, "Only players can use this");
            return;
        }
        Player player = ((Player) sender).getPlayer();
        DataAccessManager dataAccessManager = commandManager.getDataAccessManager();
        List<LandClaim> claimedChunks = dataAccessManager.getLandClaimDao().getNearbyClaimChunks(player.getLocation(), 12);

        int landClaimSize = LandClaimChunkUtilities.landClaimSize;
        int yRange = 4; // The range around the player's Y position
        String factionName = "";
        // Iterate through claimed chunks and spawn particles at their edges
        for (LandClaim claimedChunk : claimedChunks) {
            LandClaimLocation claimedChunkLocation = claimedChunk.getLocation();
            int chunk_x = claimedChunkLocation.getX();
            int chunk_z = claimedChunkLocation.getZ();
            World chunkWorld = player.getWorld();

            // Calculate the actual coordinates of the claimed chunk
            LandClaimArea claimArea = LandClaimChunkUtilities.convertChunkCoordinateToCoordinate(claimedChunkLocation);

            // Check neighboring chunks to determine if a side is bordered by another land claim
            boolean hasNeighborLeft = !hasClaimAt(claimedChunks, chunk_x - 1, chunk_z);
            boolean hasNeighborRight = !hasClaimAt(claimedChunks, chunk_x + 1, chunk_z);
            boolean hasNeighborFront = !hasClaimAt(claimedChunks, chunk_x, chunk_z - 1);
            boolean hasNeighborBack = !hasClaimAt(claimedChunks, chunk_x, chunk_z + 1);

            // Spawn particles only on sides that are not bordered by another land claim
            for (int x = claimArea.getMinX(); x <= claimArea.getMaxX(); x++) {
                for (int z = claimArea.getMinZ(); z <= claimArea.getMaxZ(); z++) {
                    for (int y = (int) player.getLocation().getY() - yRange; y <= (int) player.getLocation().getY() + yRange; y++) {
                        if ((x == claimArea.getMinX() && hasNeighborLeft) || (x == claimArea.getMaxX() && hasNeighborRight)
                                || (z == claimArea.getMinZ() && hasNeighborFront) || (z == claimArea.getMaxZ() && hasNeighborBack)) {
                            // Adjust particle location to be at the center of the block
                            double particleX = x + 0.5;
                            double particleY = y + 0.5;
                            double particleZ = z + 0.5;
                            player.spawnParticle(Particle.VILLAGER_HAPPY, particleX, particleY, particleZ, 1, 0, 0, 0, 0.5);
                        }
                    }
                }
            }


            factionName = claimedChunk.getFactionString();
            // Add an extra particle at the highest Y coordinate to indicate continuation upwards
            int highestY = (int) player.getLocation().getY() - 1;
            double particleX = chunk_x * landClaimSize + 0.5;
            double particleY = highestY + 0.5;
            double particleZ = chunk_z * landClaimSize + 0.5;
            player.spawnParticle(Particle.HEART, particleX, particleY, particleZ, 1, 0, 0, 0, 0.5);
        }

        if(!factionName.equals("")){
            MessageManager.sendMessage(player, "Nearest faction is: " + factionName);
        }
    }

    private boolean hasClaimAt(List<LandClaim> claimedChunks, int chunkX, int chunkZ) {
        for (LandClaim claimedChunk : claimedChunks) {
            LandClaimLocation claimedChunkLocation = claimedChunk.getLocation();
            if (claimedChunkLocation.getX() == chunkX && claimedChunkLocation.getZ() == chunkZ) {
                return true;
            }
        }
        return false;
    }
}
