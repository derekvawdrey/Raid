package me.amogus360.raid.Commands.Faction;

import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.LandClaim;
import me.amogus360.raid.Model.LandClaimArea;
import me.amogus360.raid.Model.LandClaimLocation;
import me.amogus360.raid.RaidCommandManager;
import me.amogus360.raid.Utilities.LandClaimChunkUtilities;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShowLandClaimsCommand extends RaidCommand {
    public ShowLandClaimsCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }

    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender, "Only players can use this");
            return;
        }
        Player player = ((Player) sender).getPlayer();
        List<LandClaim> claimedChunks = commandManager.getLandClaimDao().getNearbyClaimChunks(player.getLocation(), 12);

        int landClaimSize = LandClaimChunkUtilities.landClaimSize;
        int yRange = 3; // The range around the player's Y position

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
                            chunkWorld.spawnParticle(Particle.VILLAGER_HAPPY, particleX, particleY, particleZ, 1, 0, 0, 0, 0.5);
                        }
                    }
                }
            }

            // Add an extra particle at the highest Y coordinate to indicate continuation upwards
            int highestY = (int) player.getLocation().getY() + yRange + 1;
            double particleX = chunk_x * landClaimSize + 0.5;
            double particleY = highestY + 0.5;
            double particleZ = chunk_z * landClaimSize + 0.5;
            chunkWorld.spawnParticle(Particle.VILLAGER_HAPPY, particleX, particleY, particleZ, 1, 0, 0, 0, 0.5);
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
