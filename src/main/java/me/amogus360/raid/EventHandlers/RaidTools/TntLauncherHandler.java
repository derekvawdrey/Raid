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

public class TntLauncherHandler implements WeaponHandler {

    private final DataAccessManager dataAccessManager;

    public TntLauncherHandler(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }


    // Implement the TntLauncher logic
    @Override
    public void handle(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (RaidToolsUtils.hasTNT(player)) {
            RaidToolsUtils.consumeTNT(player);
            Location location = player.getEyeLocation();
            Vector direction = location.getDirection();
            launchTNT(location, direction);

        } else {
            MessageManager.sendMessage(player, "You don't have enough TNT!");
        }
    }

    private void launchTNT(Location location, Vector direction) {
        // Create TNT entity with a custom metadata tag
        location.setY(location.getY()-1);
        TNTPrimed primed_tnt = location.getWorld().spawn(location, TNTPrimed.class, tnt -> {
            tnt.setVelocity(direction.multiply(2)); // Adjust the multiplier to control TNT velocity
            tnt.setFuseTicks(40); // Adjust this value as needed
        });

        // Add a custom metadata tag to the TNT entity
        primed_tnt.setMetadata("TntLauncher", new FixedMetadataValue(dataAccessManager.getPlugin(), true));
    }
}