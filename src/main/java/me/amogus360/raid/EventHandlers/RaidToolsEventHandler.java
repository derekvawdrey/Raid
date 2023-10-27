package me.amogus360.raid.EventHandlers;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.EventHandlers.RaidTools.TntJumperHandler;
import me.amogus360.raid.EventHandlers.RaidTools.TntLauncherHandler;
import me.amogus360.raid.EventHandlers.RaidTools.TntShotgunHandler;
import me.amogus360.raid.EventHandlers.RaidTools.WeaponHandler;
import me.amogus360.raid.EventHandlers.RaidToolsUtils.RaidToolsUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaidToolsEventHandler implements Listener {
    private final DataAccessManager dataAccessManager;
    private final Map<String, WeaponHandler> weaponHandlers;

    public RaidToolsEventHandler(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
        this.weaponHandlers = new HashMap<>();
        initializeWeaponHandlers();
    }

    private void initializeWeaponHandlers() {
        // The lore here determines the effect
        weaponHandlers.put("Tnt Launcher", new TntLauncherHandler(dataAccessManager));
        weaponHandlers.put("Tnt Shotgun", new TntShotgunHandler(dataAccessManager));
        weaponHandlers.put("Tnt Jumper", new TntJumperHandler(dataAccessManager));
    }

    @EventHandler
    public void RaidingWeapons(PlayerInteractEvent event) {
        if (event.getAction().name().contains("RIGHT") && event.hasItem()) {
            ItemStack item = event.getItem();
            if (item.hasItemMeta()) {
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta.hasLore()) {
                    for (String lore : itemMeta.getLore()) {
                        if (weaponHandlers.containsKey(lore)) {
                            weaponHandlers.get(lore).handle(event);
                            return;
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof TNTPrimed) {
            TNTPrimed tnt = (TNTPrimed) entity;




            if (tnt.hasMetadata("RaidingTnt")) {
                // Create a list to store blocks that should be removed from the explosion
                List<Block> blocksToNotDestroy = new ArrayList<>();
                List<Block> blocksToDestroy = new ArrayList<>();
                List<Block> additionalBlocksToRegen = new ArrayList<>();
                for (Block block : RaidToolsUtils.onTNTExplode(event.getLocation(), event.blockList())) {
                    Location blockLocation = block.getLocation();

                    // Check if the block is inside a land claim (you need to implement your land claim checking logic)
                    boolean isBlockInsideLandClaim = dataAccessManager.getRaidDao().isRaidOngoingForLocation(blockLocation);

                    if (!isBlockInsideLandClaim) {
                        // Add the block to the list of blocks to remove
                        blocksToNotDestroy.add(block);
                    }else{
                        if(RaidToolsUtils.isBlockToRemove(block.getType()) && block.getType() != Material.AIR){
                            additionalBlocksToRegen.add(block);

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
                dataAccessManager.getBlockInfoDao().insertBlocks(additionalBlocksToRegen, dataAccessManager.getRaidDao().getTimestampToRebuildBlock());
                for(Block block : additionalBlocksToRegen){
                    block.setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    public void preventDamageInRaidFromTnt(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof TNTPrimed) {
            if (event.getDamager().hasMetadata("RaidingTnt")) {
                if(event.getDamager().hasMetadata("Damage")){
                    event.setDamage((int)event.getDamager().getMetadata("Damage").get(0).value());
                }
            }
        }
    }









}
