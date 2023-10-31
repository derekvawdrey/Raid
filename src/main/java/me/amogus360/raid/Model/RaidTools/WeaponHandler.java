package me.amogus360.raid.Model.RaidTools;

import me.amogus360.raid.DataAccessManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Map;

public interface WeaponHandler {

    public static enum ItemCategory {
        RAIDING,
        DEFENDING,
        MISC
    }

    public static enum ItemType{
        Enhancement,
        SingleUse,
        RaidingTool
    }

    void handle(DataAccessManager dataAccessManager, PlayerInteractEvent event, int level);

    /*
    *
    * Types of customMetaData:
    * Damage - Specify the amount of damage the tnt does
    * RaidingTnt - True/False if the tnt is a raidingTnt or not
    * NoDestroy - Determines if the block will not destroy blocks
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

    default ChatColor itemChatColor(ItemCategory itemType){
        if(itemType == ItemCategory.RAIDING) {
            return ChatColor.RED;
        }else if(itemType == ItemCategory.DEFENDING) {
            return ChatColor.GREEN;
        }else if(itemType == ItemCategory.MISC){
            return ChatColor.YELLOW;
        }else{
            return ChatColor.LIGHT_PURPLE;
        }
    }

    default int itemPrice(){
        return 200;
    }
    default ItemCategory getItemCategory(){
        return ItemCategory.RAIDING;
    }
    default ItemType getItemType(){
        return ItemType.Enhancement;
    }

    default String getItemName(){
        if(this.getItemType() == ItemType.Enhancement){
            return itemChatColor(this.getItemCategory()) + "" + ChatColor.BOLD + "Item Enhancement";
        }else if(this.getItemType() == ItemType.SingleUse){
            return itemChatColor(this.getItemCategory()) + "" + ChatColor.BOLD + "Single Use Item";
        }else if(this.getItemType() == ItemType.RaidingTool){
            return itemChatColor(this.getItemCategory()) + "" + ChatColor.BOLD + "Raiding Tool";
        }
        return itemChatColor(this.getItemCategory()) + "" + ChatColor.BOLD + "Unknown";
    }

    public String getActivationLore();

}