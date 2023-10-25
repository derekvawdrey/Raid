package me.amogus360.raid.EventHandlers.RaidToolsUtils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RaidToolsUtils {

    public static List<Block> onTNTExplode(Location source, List<Block> blocks) {
        int radius = (int) Math.ceil(5);


        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = new Location(source.getWorld(), x + source.getX(), y + source.getY(), z + source.getZ());
                    if (source.distance(loc) <= radius) {
                        if(isBlockToRemove(loc.getBlock().getType())) blocks.add(loc.getBlock());
                    }
                }
            }
        }
        return blocks;
    }

    public static boolean isBlockToRemove(Material material) {
        // Define which block types should be removed during the explosion
        Material[] removableBlocks = {
                Material.OBSIDIAN, // Remove obsidian
                Material.WATER,
                Material.LAVA,
                Material.CRYING_OBSIDIAN,
                Material.END_CRYSTAL
                // Add more block types as needed
        };

        for (Material blockType : removableBlocks) {
            if (material == blockType) {
                return true;
            }
        }

        return false;
    }


    public static boolean hasTNT(Player player) {
        ItemStack[] inventory = player.getInventory().getContents();
        for (ItemStack itemStack : inventory) {
            if (itemStack != null && itemStack.getType() == Material.TNT) {
                return true;
            }
        }
        return false;
    }

    public static void consumeTNT(Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack != null && itemStack.getType() == Material.TNT) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                if (itemStack.getAmount() <= 0) {
                    player.getInventory().setItem(i, new ItemStack(Material.AIR));
                }
                return;
            }
        }
    }

}
