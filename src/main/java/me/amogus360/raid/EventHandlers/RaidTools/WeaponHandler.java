package me.amogus360.raid.EventHandlers.RaidTools;

import me.amogus360.raid.DataAccessManager;
import org.bukkit.Location;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Map;

public interface WeaponHandler {

    void handle(PlayerInteractEvent event);

    /*
    *
    * Types of customMetaData:
    * Damage
    * RaidingTnt
    * NoDestroy
    *
    *
    * */
    default void createTnt(Location location, Vector direction, int numProjectiles, double spreadAngle, int fuseTicks, Map<String, Object> customMetadata, DataAccessManager dataAccessManager) {
        location.setY(location.getY() - 1);
        for (int i = 0; i < numProjectiles; i++) {
            double spread = Math.toRadians((Math.random() - 0.5) * spreadAngle);
            double x = direction.getX() * Math.cos(spread) - direction.getZ() * Math.sin(spread);
            double z = direction.getX() * Math.sin(spread) + direction.getZ() * Math.cos(spread);
            Vector spreadDirection = new Vector(x, 0, z);

            TNTPrimed primedTnt = location.getWorld().spawn(location, TNTPrimed.class, tnt -> {
                tnt.setVelocity(spreadDirection.multiply(2)); // Adjust the multiplier to control TNT velocity
                tnt.setFuseTicks(fuseTicks); // Adjust this value as needed
            });

            primedTnt.setMetadata("RaidingTnt", new FixedMetadataValue(dataAccessManager.getPlugin(), true));

            // Add custom metadata to the TNT entity
            for (Map.Entry<String, Object> entry : customMetadata.entrySet()) {
                primedTnt.setMetadata(entry.getKey(), new FixedMetadataValue(dataAccessManager.getPlugin(), entry.getValue()));
            }
        }
    }

}