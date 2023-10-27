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

import java.util.HashMap;
import java.util.Map;

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
        int fuseTicks = 40;

        Map<String, Object> customMetadata = new HashMap<>();
        customMetadata.put("RaidingTnt", true);
        customMetadata.put("Damage", 0);

        createTnt(location, direction, 1, 0.0, fuseTicks, customMetadata, dataAccessManager);
    }

}