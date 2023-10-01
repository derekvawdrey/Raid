package me.amogus360.raid.EventHandlers;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.LandClaim;
import me.amogus360.raid.TableManager;
import me.amogus360.raid.Utilities.LandClaimChunkUtilities;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

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
        int playerFactionId = dataAccessManager.getFactionDao().getFactionInfoByPlayerUUID(event.getPlayer().getUniqueId()).getFactionId();
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
        int playerFactionId = dataAccessManager.getFactionDao().getFactionInfoByPlayerUUID(event.getPlayer().getUniqueId()).getFactionId();
        if (landClaim != null && playerFactionId != landClaim.getFactionId()) {
            event.setCancelled(true); // Cancel the block break event
            MessageManager.sendMessage(event.getPlayer(),"You can't place blocks in another faction's territory!");
        }
    }
}
