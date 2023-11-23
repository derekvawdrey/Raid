package me.amogus360.raid.Model.Items.Misc;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.Items.ItemHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TeleportationShard implements ItemHandler {
    @Override
    public void handle(DataAccessManager dataAccessManager,  PlayerInteractEvent event, int level) {
        Player player = event.getPlayer();
        Location location = player.getLocation(); // You may need to adjust this to the desired location

        // Check if the player has the item in their hand
        ItemStack itemInHand = event.getItem();
        if (itemInHand != null) {
            initiateTeleportPearl(dataAccessManager,player);
            // Decrease the item stack by one
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        }
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SingleUse;
    }

    @Override
    public String getActivationLore() {
        return itemChatColor(this.getItemCategory()) + "Teleportation";
    }

    @Override
    public ItemCategory getItemCategory(){
        return ItemCategory.MISC;
    }

    @Override
    public Material getItemMaterial(){
        return Material.ECHO_SHARD;
    }

    @Override
    public String getItemName(){
        return itemChatColor(this.getItemCategory()) + "" + ChatColor.BOLD + "Shard of Teleportation";
    }

    public static void initiateTeleportPearl(DataAccessManager dataAccessManager, Player activator) {
        // Create a new teleport request
        TeleportRequest request = new TeleportRequest(activator);

        // Send a message to the activator
        MessageManager.sendMessage(activator,"Type the name of the player you want to teleport to.");

        // Save the current state to indicate that the player is in the teleport process
        dataAccessManager.getItemDao().putIntoTeleportRequests(activator.getName(), request);
    }
}


