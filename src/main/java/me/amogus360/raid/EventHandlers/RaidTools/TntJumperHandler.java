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

public class TntJumperHandler implements WeaponHandler {

    private final DataAccessManager dataAccessManager;

    public TntJumperHandler(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }


    // Implement the TntJumper logic
    @Override
    public void handle(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (RaidToolsUtils.hasTNT(player)) {
            RaidToolsUtils.consumeTNT(player);
            Location location = player.getEyeLocation();
            Vector direction = location.getDirection();
            tntJumper(location, direction);

        } else {
            MessageManager.sendMessage(player, "You don't have enough TNT!");
        }
    }

    private void tntJumper(Location location, Vector direction) {
        int numProjectiles = 3;
        int fuseTicks = 2;

        Map<String, Object> customMetadata = new HashMap<>();
        customMetadata.put("RaidingTnt", true);
        customMetadata.put("NoDestroy", true);
        customMetadata.put("Damage", 1);

        createTnt(location, direction, numProjectiles, 0.0, fuseTicks, customMetadata, dataAccessManager);
    }

}