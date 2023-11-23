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

public class TntShotgunHandler implements ItemHandler {

    // Implement the TntShotgun logic
    @Override
    public void handle(DataAccessManager dataAccessManager, PlayerInteractEvent event, int level) {
        Player player = event.getPlayer();
        if (RaidToolsUtils.hasTNT(player)) {
            RaidToolsUtils.consumeTNT(player);
            Location location = player.getEyeLocation();
            Vector direction = location.getDirection();
            launchTNTShotgun(dataAccessManager, location, direction);

        } else {
            MessageManager.sendMessage(player, "You don't have enough TNT!");
        }
    }

    private void launchTNTShotgun(DataAccessManager dataAccessManager, Location location, Vector direction) {
        int numProjectiles = 3;
        double spreadAngle = 60.0;
        int fuseTicks = 40 + (int)(Math.random() * 49 + 1);

        Map<String, Object> customMetadata = new HashMap<>();
        customMetadata.put("RaidingTnt", true);
        customMetadata.put("Damage", 0);

        createTnt(location, direction, numProjectiles, spreadAngle, fuseTicks, customMetadata, dataAccessManager);
    }

    @Override
    public String getActivationLore() {
        return itemChatColor(this.getItemCategory()) + "Tnt Scattershot";
    }
    @Override
    public int getItemPrice(){ return 750; }

}
