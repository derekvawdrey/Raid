package me.amogus360.raid.EventHandlers.RaidTools;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.EventHandlers.RaidToolsUtils.RaidToolsUtils;
import me.amogus360.raid.MessageManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class TntShotgunHandler implements WeaponHandler {


    private final DataAccessManager dataAccessManager;

    public TntShotgunHandler(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }

    // Implement the TntShotgun logic
    @Override
    public void handle(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (RaidToolsUtils.hasTNT(player)) {
            RaidToolsUtils.consumeTNT(player);
            Location location = player.getEyeLocation();
            Vector direction = location.getDirection();
            launchTNTShotgun(location, direction);

        } else {
            MessageManager.sendMessage(player, "You don't have enough TNT!");
        }
    }

    private void launchTNTShotgun(Location location, Vector direction) {
        int numProjectiles = 3; // Number of TNT projectiles in the shotgun
        double spreadAngle = 60.0; // The angle of the spread in degrees
        location.setY(location.getY()-1);
        for (int i = 0; i < numProjectiles; i++) {
            double spread = Math.toRadians((Math.random() - 0.5) * spreadAngle);
            double x = direction.getX() * Math.cos(spread) - direction.getZ() * Math.sin(spread);
            double z = direction.getX() * Math.sin(spread) + direction.getZ() * Math.cos(spread);
            Vector spreadDirection = new Vector(x, 0, z);

            TNTPrimed primed_tnt = location.getWorld().spawn(location, TNTPrimed.class, tnt -> {
                tnt.setVelocity(spreadDirection.multiply(2)); // Adjust the multiplier to control TNT velocity
                tnt.setFuseTicks(40 + (int)(Math.random() * 49 + 1)); // Adjust this value as needed
            });

            primed_tnt.setMetadata("TntLauncher", new FixedMetadataValue(dataAccessManager.getPlugin(), true));
        }
    }
}
