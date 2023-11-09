package me.amogus360.raid.Model.Items.Raiding;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.Model.Items.ItemHandler;
import me.amogus360.raid.Utilities.RaidToolsUtils;
import me.amogus360.raid.MessageManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class TntLauncherHandler implements ItemHandler {


    // Implement the TntLauncher logic
    @Override
    public void handle(DataAccessManager dataAccessManager, PlayerInteractEvent event, int level) {
        Player player = event.getPlayer();
        if (RaidToolsUtils.hasTNT(player)) {
            RaidToolsUtils.consumeTNT(player);
            Location location = player.getEyeLocation();
            Vector direction = location.getDirection();
            launchTNT(dataAccessManager,location, direction);

        } else {
            MessageManager.sendMessage(player, "You don't have enough TNT!");
        }
    }

    private void launchTNT(DataAccessManager dataAccessManager, Location location, Vector direction) {
        int fuseTicks = 40;

        Map<String, Object> customMetadata = new HashMap<>();
        customMetadata.put("RaidingTnt", true);
        customMetadata.put("Damage", 0);

        createTnt(location, direction, 1, 0.0, fuseTicks, customMetadata, dataAccessManager);
    }

    @Override
    public String getActivationLore() {
        return itemChatColor(this.getItemCategory()) + "Tnt Cannon";
    }

}