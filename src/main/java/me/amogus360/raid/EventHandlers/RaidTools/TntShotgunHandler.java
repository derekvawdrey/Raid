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
        int numProjectiles = 3;
        double spreadAngle = 60.0;
        int fuseTicks = 40 + (int)(Math.random() * 49 + 1);

        Map<String, Object> customMetadata = new HashMap<>();
        customMetadata.put("RaidingTnt", true);
        customMetadata.put("Damage", 0);

        createTnt(location, direction, numProjectiles, spreadAngle, fuseTicks, customMetadata, dataAccessManager);
    }

}
