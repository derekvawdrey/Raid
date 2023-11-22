package me.amogus360.raid.Model.Items.Misc;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.Model.Items.ItemHandler;
import me.amogus360.raid.Utilities.DefenderUtilities;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TeleportPearl implements ItemHandler {
    @Override
    public void handle(DataAccessManager dataAccessManager,  PlayerInteractEvent event, int level) {
        Player player = event.getPlayer();
        Location location = player.getLocation(); // You may need to adjust this to the desired location
        int faction_id = dataAccessManager.getFactionDao().getFactionIdByPlayerUUID(player.getUniqueId()); // Replace with the actual faction ID

        // Check if the player has the item in their hand
        ItemStack itemInHand = event.getItem();
        if (itemInHand != null) {

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
        return itemChatColor(this.getItemCategory()) + "Summon Skeleton Defender";
    }
}
