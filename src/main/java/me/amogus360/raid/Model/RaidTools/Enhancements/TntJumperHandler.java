package me.amogus360.raid.Model.RaidTools.Enhancements;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.Model.RaidTools.WeaponHandler;
import me.amogus360.raid.Utilities.RaidToolsUtils;
import me.amogus360.raid.MessageManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

public class TntJumperHandler implements WeaponHandler {


    // Implement the TntJumper logic
    @Override
    public void handle(DataAccessManager dataAccessManager, PlayerInteractEvent event, int level) {
        Player player = event.getPlayer();
        if (RaidToolsUtils.hasTNT(player)) {
            RaidToolsUtils.consumeTNT(player);
            Location location = player.getEyeLocation();
            Vector direction = location.getDirection();
            tntJumper(dataAccessManager, location, direction, level);

        } else {
            MessageManager.sendMessage(player, "You don't have enough TNT!");
        }
    }

    private void tntJumper(DataAccessManager dataAccessManager, Location location, Vector direction, int level) {
        int numProjectiles = 2 + level;
        int fuseTicks = 3;

        Map<String, Object> customMetadata = new HashMap<>();
        customMetadata.put("RaidingTnt", true);
        customMetadata.put("NoDestroy", true);
        customMetadata.put("Damage", 1);

        createTnt(location, direction, numProjectiles, 0.0, fuseTicks, customMetadata, dataAccessManager);
    }

    @Override
    public String getActivationLore() {
        return this.itemChatColor(this.getItemCategory()) + "Tnt Jumper";
    }
    @Override
    public ItemCategory getItemCategory(){
        return ItemCategory.MISC;
    }
}