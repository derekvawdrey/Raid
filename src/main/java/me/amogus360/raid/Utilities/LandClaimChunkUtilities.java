package me.amogus360.raid.Utilities;

import me.amogus360.raid.Model.LandClaim.LandClaimArea;
import me.amogus360.raid.Model.LandClaim.LandClaimLocation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LandClaimChunkUtilities {
    public static int landClaimSize = 12;
    public static Location convertToChunkCoordinate(Location location){
        int chunk_x = (int) Math.floor((double) location.getBlockX() / (double)landClaimSize);
        int chunk_y = (int) Math.floor((double) location.getBlockY() / (double)landClaimSize);
        int chunk_z = (int) Math.floor((double) location.getBlockZ() / (double)landClaimSize);
        World chunk_world = location.getWorld();
        Location chunkLocation = new Location(chunk_world,(double)chunk_x,(double)chunk_y,(double)chunk_z);
        return chunkLocation;
    }



    public static LandClaimArea convertChunkCoordinateToCoordinate(LandClaimLocation claimedChunkLocation){
        int chunk_x = claimedChunkLocation.getX();
        int chunk_y = claimedChunkLocation.getY();
        int chunk_z = claimedChunkLocation.getZ();

        // Calculate the actual coordinates of the claimed chunk
        int minX = chunk_x * landClaimSize;
        int minZ = chunk_z * landClaimSize;
        int minY = chunk_y * landClaimSize;
        int maxX = minX + landClaimSize - 1;
        int maxZ = minZ + landClaimSize - 1;
        int maxY = minY + landClaimSize - 1;

        return new LandClaimArea(minX,minY,minZ,maxX,maxY,maxZ);
    }



}
