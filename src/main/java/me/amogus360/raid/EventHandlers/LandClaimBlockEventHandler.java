package me.amogus360.raid.EventHandlers;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.FactionInfo;
import me.amogus360.raid.Model.LandClaim;
import me.amogus360.raid.Model.LandClaimArea;
import me.amogus360.raid.TableManager;
import me.amogus360.raid.Utilities.LandClaimChunkUtilities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class LandClaimBlockEventHandler implements Listener {

    private final DataAccessManager dataAccessManager; // Assuming you have a TableManager instance

    public LandClaimBlockEventHandler(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        LandClaimChunkUtilities.convertToChunkCoordinate(blockLocation);
        // Check if the player's faction owns the land at the block location

        LandClaim landClaim = dataAccessManager.getLandClaimDao().getClaimChunk(blockLocation);
        FactionInfo playerFactionInfo = dataAccessManager.getFactionDao().getFactionInfoByPlayerUUID(event.getPlayer().getUniqueId());
        int playerFactionId = 0;
        if(playerFactionInfo != null){
            playerFactionId = playerFactionInfo.getFactionId();
        }
        if (landClaim != null && playerFactionId != landClaim.getFactionId()) {
            event.setCancelled(true); // Cancel the block break event
            MessageManager.sendMessage(event.getPlayer(),"You can't break blocks in another faction's territory!");
        }

    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        LandClaimChunkUtilities.convertToChunkCoordinate(blockLocation);
        // Check if the player's faction owns the land at the block location

        LandClaim landClaim = dataAccessManager.getLandClaimDao().getClaimChunk(blockLocation);
        FactionInfo playerFactionInfo = dataAccessManager.getFactionDao().getFactionInfoByPlayerUUID(event.getPlayer().getUniqueId());
        int playerFactionId = 0;
        if(playerFactionInfo != null){
            playerFactionId = playerFactionInfo.getFactionId();
        }
        if (landClaim != null && playerFactionId != landClaim.getFactionId()) {
            event.setCancelled(true); // Cancel the block break event
            MessageManager.sendMessage(event.getPlayer(),"You can't place blocks in another faction's territory!");
        }
    }
    // Implement lava flow/water flow prevention, tnt/creeper explosion prevention

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        if (entity == null) {
            return;
        }

        Location explosionLocation = entity.getLocation();

        // Check if the explosion occurred inside a land claim (you need to implement your land claim checking logic)
        boolean isInsideLandClaim = dataAccessManager.getLandClaimDao().isClaimed(explosionLocation);

        if (!isInsideLandClaim) {
            // The explosion occurred outside the land claim, prevent block damage
            List<Block> blocksToRemove = new ArrayList<>();

            for (Block block : event.blockList()) {
                Location blockLocation = block.getLocation();

                // Check if the block is inside a land claim (you need to implement your land claim checking logic)
                boolean isBlockInsideLandClaim = dataAccessManager.getLandClaimDao().isClaimed(blockLocation);

                if (isBlockInsideLandClaim) {
                    // Add the block to the list of blocks to remove
                    blocksToRemove.add(block);
                }
            }

            // Remove all blocks inside land claims from the block list
            event.blockList().removeAll(blocksToRemove);
        }else{
            List<Block> blocksToRegenerate = new ArrayList<>();

            for (Block block : event.blockList()) {
                Location blockLocation = block.getLocation();
                boolean isBlockInsideLandClaim = dataAccessManager.getLandClaimDao().isClaimed(blockLocation);

                if (isBlockInsideLandClaim) {
                    if(block.getType() != Material.AIR && block.getType() != Material.TNT) blocksToRegenerate.add(block);
                }
            }
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            dataAccessManager.getBlockInfoDao().insertBlocks(blocksToRegenerate, timestamp);
        }
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        if(!dataAccessManager.getLandClaimDao().isClaimed(event.getBlock().getLocation())) {
            Block toBlock = event.getToBlock();
            Location toLocation = toBlock.getLocation();
            if (dataAccessManager.getLandClaimDao().isClaimed(toLocation)) {
                event.setCancelled(true); // Cancel the liquid flow
            }
        }
    }
}

