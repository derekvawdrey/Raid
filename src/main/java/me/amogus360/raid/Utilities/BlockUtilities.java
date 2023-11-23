package me.amogus360.raid.Utilities;

import me.amogus360.raid.BlockChanger.BlockChanger;
import me.amogus360.raid.Model.Block.BlockInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if (block.getState() instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) block.getState();

            if (holder instanceof DoubleChest) {
                DoubleChest doubleChest = (DoubleChest) holder;
                InventoryHolder leftSide = doubleChest.getLeftSide();
                InventoryHolder rightSide = doubleChest.getRightSide();

                // Assuming the block you are checking is the left side
                if (holder.equals(leftSide)) {
                    handleSingleChestInventory(blockInfo, leftSide.getInventory());
                } else if (holder.equals(rightSide)) {
                    handleSingleChestInventory(blockInfo, rightSide.getInventory());
                }
            } else {
                handleSingleChestInventory(blockInfo, holder.getInventory());
            }
        }

        return blockInfo;
    }

    private static void handleSingleChestInventory(BlockInfo blockInfo, Inventory inventory) {
        blockInfo.setChestContents(new ArrayList<>());

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                blockInfo.getChestContents().add(item.clone());
            }
        }

        // Stop the chest from spitting out its contents, stopping duping from occurring
        inventory.clear();
    }



    public static void placeBlockArray(List<BlockInfo> blockInfoArray){
        Map<String,World> worldMap = new HashMap<String,World>();
        Map<BlockInfo, World> blockInfoToBlockMap = new HashMap<>();
        //TODO: Set block material data
        // Here we will prevent loading worlds more than 1 time
        for(BlockInfo blockInfo : blockInfoArray ){
            // Make sure we aren't loading a world every time we deserialize a blockInfo block
            String worldName = blockInfo.getWorldName();
            if(!worldMap.containsKey(worldName)) worldMap.put(worldName, Bukkit.getWorld(worldName));
            placeBlock(blockInfo, worldMap.get(worldName));
            if(blockInfo.getChestContents() != null && !blockInfo.getChestContents().isEmpty()) blockInfoToBlockMap.put(blockInfo,worldMap.get(worldName));
        }

        blockInfoToBlockMap.forEach((key, value) -> {
            insertInventoryToBlock(key,value);
        });
    }


    public static void placeBlock(BlockInfo blockInfo, World world) {
        int x = blockInfo.getX();
        int y = blockInfo.getY();
        int z = blockInfo.getZ();

        // TOOD: If a block is destroyed with tnt, the curbed stairs wont be curved as they will default to non-curved stairs
        Location blockLocation = new Location(world,x,y,z);
        Material material = Material.getMaterial(blockInfo.getMaterial());
        BlockData blockData = Bukkit.createBlockData(blockInfo.getBlockData());
        if(material != Material.AIR) {
            // This is a faster method but I need to figure out how to support BlockData
            // ItemStack block = new ItemStack(material);
            // BlockChanger.setBlockAsynchronously(blockLocation, block, false);

            // TODO: Test and see if this actually works.
            world.getBlockAt(blockLocation).setBlockData(blockData, true);
        }
        //return null;
    }

    public static void insertInventoryToBlock(BlockInfo blockInfo, World world){
        int x = blockInfo.getX();
        int y = blockInfo.getY();
        int z = blockInfo.getZ();
        Block block = world.getBlockAt(x,y,z);

        if (block.getState() instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) block.getState();
            List<ItemStack> chestContents = blockInfo.getChestContents();

            if (chestContents != null) {
                System.out.println("Setting chest contents: " + chestContents);
                for (int i = 0; i < chestContents.size(); i++) {
                    holder.getInventory().setItem(i, chestContents.get(i));
                }
            }
        }
    }

}
