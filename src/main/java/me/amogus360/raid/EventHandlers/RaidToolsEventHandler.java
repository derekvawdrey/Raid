package me.amogus360.raid.EventHandlers;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RaidToolsEventHandler implements Listener {

    private final DataAccessManager dataAccessManager; // Assuming you have a TableManager instance

    public RaidToolsEventHandler(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }

    @EventHandler
    public void TntLauncherCode(PlayerInteractEvent event) {
        if (event.getAction().name().contains("RIGHT") && event.hasItem()) {
            if(checkTntLaunch(event)){}
            else if(checkTntShotgun(event)){}
            else if(checkTntJumper(event)){}
        }
    }

    private boolean checkTntLaunch(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta.hasLore() && itemMeta.getLore().contains("Tnt Launcher")) {
                Player player = event.getPlayer();
                if (hasTNT(player)) {
                    consumeTNT(player);
                    Location location = player.getEyeLocation();
                    Vector direction = location.getDirection();
                    launchTNT(location, direction);

                } else {
                    MessageManager.sendMessage(player, "You don't have enough TNT!");
                }
                return true;
            }
        }
        return false;
    }

    private boolean checkTntShotgun(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta.hasLore() && itemMeta.getLore().contains("Tnt Shotgun")) {
                Player player = event.getPlayer();
                if (hasTNT(player)) {
                    consumeTNT(player);
                    Location location = player.getEyeLocation();
                    Vector direction = location.getDirection();
                    launchTNTShotgun(location, direction);

                } else {
                    MessageManager.sendMessage(player, "You don't have enough TNT!");
                }
                return true;
            }
        }
        return false;
    }
    private boolean checkTntJumper(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta.hasLore() && itemMeta.getLore().contains("Tnt Jumpers")) {
                Player player = event.getPlayer();
                if (hasTNT(player)) {
                    consumeTNT(player);
                    Location location = player.getEyeLocation();
                    Vector direction = location.getDirection();
                    tntJumper(location, direction);

                } else {
                    MessageManager.sendMessage(player, "You don't have enough TNT!");
                }
                return true;
            }
        }
        return false;
    }



    private List<Block> onTNTExplode(Location source, List<Block> blocks) {
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


    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof TNTPrimed) {
            TNTPrimed tnt = (TNTPrimed) entity;




            if (tnt.hasMetadata("TntLauncher")) {
                // Create a list to store blocks that should be removed from the explosion
                List<Block> blocksToNotDestroy = new ArrayList<>();
                List<Block> blocksToDestroy = new ArrayList<>();
                for (Block block : onTNTExplode(event.getLocation(), event.blockList())) {
                    Location blockLocation = block.getLocation();

                    // Check if the block is inside a land claim (you need to implement your land claim checking logic)
                    boolean isBlockInsideLandClaim = dataAccessManager.getRaidDao().isRaidOngoingForLocation(blockLocation);

                    if (!isBlockInsideLandClaim) {
                        // Add the block to the list of blocks to remove
                        blocksToNotDestroy.add(block);
                    }else{
                        if(isBlockToRemove(block.getType()) && block.getType() != Material.AIR){
                            blocksToDestroy.add(block);
                            blockLocation.getBlock().setType(Material.AIR);
                        }else{
                            if(tnt.hasMetadata("NoDestroy")){
                                blocksToNotDestroy.add(block);
                            }
                        }
                    }
                }

                // Remove all blocks inside land claims from the event.blockList()
                event.blockList().removeAll(blocksToNotDestroy);

                // Set the explosion yield to 0 to prevent further block damage
                event.setYield(0);

                blocksToDestroy.addAll(event.blockList());

                // Insert information about the remaining blocks (outside of land claims) into your data access manager
                dataAccessManager.getBlockInfoDao().insertBlocks(blocksToDestroy, dataAccessManager.getRaidDao().getTimestampToRebuildBlock());
            }
        }
    }

    @EventHandler
    public void preventDamageInRaidFromTnt(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof TNTPrimed) {
            if (event.getDamager().hasMetadata("TntLauncher")) {

                event.setDamage(0);

                if(event.getDamager().hasMetadata("Hurt1")){
                    event.setDamage(1);
                }

            }
        }
    }


    private boolean isBlockToRemove(Material material) {
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


    private boolean hasTNT(Player player) {
        ItemStack[] inventory = player.getInventory().getContents();
        for (ItemStack itemStack : inventory) {
            if (itemStack != null && itemStack.getType() == Material.TNT) {
                return true;
            }
        }
        return false;
    }

    private void consumeTNT(Player player) {
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

    private void launchTNT(Location location, Vector direction) {
        // Create TNT entity with a custom metadata tag
        location.setY(location.getY()-1);
        TNTPrimed primed_tnt = location.getWorld().spawn(location, TNTPrimed.class, tnt -> {
            tnt.setVelocity(direction.multiply(2)); // Adjust the multiplier to control TNT velocity
            tnt.setFuseTicks(40); // Adjust this value as needed
        });

        // Add a custom metadata tag to the TNT entity
        primed_tnt.setMetadata("TntLauncher", new FixedMetadataValue(dataAccessManager.getPlugin(), true));
    }

    private void launchTNTShotgun(Location location, Vector direction) {
        int numProjectiles = 5; // Number of TNT projectiles in the shotgun
        double spreadAngle = 30.0; // The angle of the spread in degrees
        location.setY(location.getY()-1);
        for (int i = 0; i < numProjectiles; i++) {
            double spread = Math.toRadians((Math.random() - 0.5) * spreadAngle);
            double x = direction.getX() * Math.cos(spread) - direction.getZ() * Math.sin(spread);
            double z = direction.getX() * Math.sin(spread) + direction.getZ() * Math.cos(spread);
            Vector spreadDirection = new Vector(x, 0, z);

            TNTPrimed primed_tnt = location.getWorld().spawn(location, TNTPrimed.class, tnt -> {
                tnt.setVelocity(spreadDirection.multiply(2)); // Adjust the multiplier to control TNT velocity
                tnt.setFuseTicks(40); // Adjust this value as needed
            });

            primed_tnt.setMetadata("TntLauncher", new FixedMetadataValue(dataAccessManager.getPlugin(), true));
        }
    }

    private void tntJumper(Location location, Vector direction) {
        // Create TNT entity with a custom metadata tag
        location.setY(location.getY()-1);
        int numProjectiles = 3; // Number of TNT projectiles in the shotgun

        for (int i = 0; i < numProjectiles; i++) {
            TNTPrimed primed_tnt = location.getWorld().spawn(location, TNTPrimed.class, tnt -> {
                tnt.setVelocity(direction.multiply(2)); // Adjust the multiplier to control TNT velocity
                tnt.setFuseTicks(2); // Adjust this value as needed
            });
            primed_tnt.setMetadata("TntLauncher", new FixedMetadataValue(dataAccessManager.getPlugin(), true));
            primed_tnt.setMetadata("NoDestroy", new FixedMetadataValue(dataAccessManager.getPlugin(), true));
            primed_tnt.setMetadata("Hurt1", new FixedMetadataValue(dataAccessManager.getPlugin(), true));
        }
    }



}
