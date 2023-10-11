package me.amogus360.raid.Utilities;

import me.amogus360.raid.BlockChanger.BlockChanger;
import me.amogus360.raid.Model.Block.BlockInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
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
            blockInfo.setChestContents(new ArrayList<>());

            for (ItemStack item : holder.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    blockInfo.getChestContents().add(item.clone());
                }
            }
        }


        return blockInfo;
    }
    public static void placeBlockArray(List<BlockInfo> blockInfoArray){
        Map<String,World> worldMap = new HashMap<String,World>();
        Map<BlockInfo, World> blockInfoToBlockMap = new HashMap<>();
        System.out.println("Starting block place");
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


        Location blockLocation = new Location(world,x,y,z);
        Material material = Material.getMaterial(blockInfo.getMaterial());
        BlockChanger.setBlockAsynchronously(blockLocation, new ItemStack(material), false);
        world.getBlockAt(x,y,z).setBlockData(Bukkit.createBlockData(blockInfo.getBlockData()));
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
