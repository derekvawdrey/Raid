package me.amogus360.raid.Utilities;

import me.amogus360.raid.Model.Block.BlockInfo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class BlockUtilities {
    public static BlockInfo convertBlockToBlockInfo(World world, int x, int y, int z, Block block) {
        BlockInfo blockInfo = new BlockInfo();

        // Basic block information
        blockInfo.setX(x);
        blockInfo.setY(y);
        blockInfo.setZ(z);
        blockInfo.setMaterial(block.getType().toString());
        blockInfo.setWorldName(block.getWorld().getName());
        // Block data (if available)
        if (block.getBlockData() != null) {
            blockInfo.setBlockData(block.getBlockData().getAsString());
        }

        // Chest contents (if the block is a chest)
        if (block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();
            blockInfo.setChestContents(new ArrayList<>());

            for (ItemStack item : chest.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    blockInfo.getChestContents().add(item.clone());
                }
            }
        }

        return blockInfo;
    }

    public static void placeBlock(BlockInfo blockInfo) {
        int x = blockInfo.getX();
        int y = blockInfo.getY();
        int z = blockInfo.getZ();


        Block block = Bukkit.getWorld(blockInfo.getWorldName()).getBlockAt(x, y, z);
        Material material = Material.getMaterial(blockInfo.getMaterial());

        if (material != null) {

            block.setType(material);

            if (blockInfo.getBlockData() != null) {
                try {
                    // Parse the block data and set it using BlockData
                    BlockData blockData = Bukkit.createBlockData(blockInfo.getBlockData());
                    block.setBlockData(blockData, true);

                } catch (IllegalArgumentException e) {
                    // Handle invalid block data
                    e.printStackTrace();
                }
            }

            if (block.getState() instanceof Chest) {
                Chest chest = (Chest) block.getState();
                List<ItemStack> chestContents = blockInfo.getChestContents();

                if (chestContents != null) {
                    System.out.println("Setting chest contents: " + chestContents);
                    for (int i = 0; i < chestContents.size(); i++) {
                        chest.getInventory().setItem(i, chestContents.get(i));
                    }
                }
            }
        }
    }

}
