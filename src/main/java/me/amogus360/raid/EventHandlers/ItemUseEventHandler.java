package me.amogus360.raid.EventHandlers;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.Model.Items.Misc.TeleportationShard;
import me.amogus360.raid.Model.Items.Raiding.TntJumperHandler;
import me.amogus360.raid.Model.Items.Raiding.TntLauncherHandler;
import me.amogus360.raid.Model.Items.Raiding.TntShotgunHandler;
import me.amogus360.raid.Model.Items.Defending.SummonSkeletonDefender;
import me.amogus360.raid.Model.Items.ItemHandler;
import me.amogus360.raid.Model.Items.Misc.TeleportRequest;
import me.amogus360.raid.Utilities.DefenderUtilities;
import me.amogus360.raid.Utilities.RaidToolsUtils;
import me.amogus360.raid.Model.LoreLevelInformation;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ItemUseEventHandler implements Listener {
    private final DataAccessManager dataAccessManager;
    private final Map<String, ItemHandler> weaponHandlers;

    public ItemUseEventHandler(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
        this.weaponHandlers = new HashMap<>();
        initializeWeaponHandlers();
    }

    private void initializeWeaponHandlers() {

        TntLauncherHandler tntLauncherHandler = new TntLauncherHandler();
        TntShotgunHandler tntShotgunHandler = new TntShotgunHandler();
        TntJumperHandler tntJumperHandler = new TntJumperHandler();
        SummonSkeletonDefender summonSkeletonDefender = new SummonSkeletonDefender();
        TeleportationShard teleportShard = new TeleportationShard();

        // The lore here determines the effect
        weaponHandlers.put(tntJumperHandler.getActivationLore(), tntJumperHandler);
        weaponHandlers.put(tntShotgunHandler.getActivationLore(), tntShotgunHandler);
        weaponHandlers.put(tntLauncherHandler.getActivationLore(), tntLauncherHandler);
        weaponHandlers.put(summonSkeletonDefender.getActivationLore(), summonSkeletonDefender);
        weaponHandlers.put(teleportShard.getActivationLore(), teleportShard);
    }


    @EventHandler
    public void RaidingWeapons(PlayerInteractEvent event) {
        if (event.getAction().name().contains("RIGHT") && event.hasItem()) {
            ItemStack item = event.getItem();

            // For raiding tools
            if(item.getType() == Material.GOAT_HORN) {
                if (item.hasItemMeta() && !event.getPlayer().hasCooldown(Material.GOAT_HORN)) {
                    ItemMeta itemMeta = item.getItemMeta();
                    List<LoreLevelInformation> loreLevels = RaidToolsUtils.getLoreLevels(itemMeta);

                    for (LoreLevelInformation loreInfo : loreLevels) {
                        String enhancementType = loreInfo.getEnhancementType();
                        if (weaponHandlers.containsKey(enhancementType)) {
                            weaponHandlers.get(enhancementType).handle(dataAccessManager, event, loreInfo.getLevel());
                        }
                    }
                }
            }else{
                // For all other tools/items
                ItemMeta itemMeta = item.getItemMeta();
                List<LoreLevelInformation> loreLevels = RaidToolsUtils.getLoreLevels(itemMeta);

                for (LoreLevelInformation loreInfo : loreLevels) {
                    String enhancementType = loreInfo.getEnhancementType();
                    if (weaponHandlers.containsKey(enhancementType)) {
                        weaponHandlers.get(enhancementType).handle(dataAccessManager, event, loreInfo.getLevel());
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

                boolean defenderNearby = false;
                Entity defender = null;
                int radius = DefenderUtilities.defenderProtectionRadius;
                for (Entity nearbyEntity : tnt.getNearbyEntities(radius, radius, radius)) {
                    PersistentDataContainer dataContainer = nearbyEntity.getPersistentDataContainer();
                    if (dataContainer.has(new NamespacedKey(dataAccessManager.getPlugin(), "defender"), PersistentDataType.INTEGER)) {
                        defenderNearby = true;
                        defender = nearbyEntity;
                        break;
                    }
                }

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
                    } else {
                        // If a defender is nearby stop blocks from being destroyed that are below the defender.
                        if(defenderNearby && isLocationBelow(defender.getLocation(), block.getLocation())){
                            blocksToNotDestroy.add(block);
                        }else {

                            // Check if the block is a double chest
                            if (block.getState() instanceof Chest) {
                                Chest chest = (Chest) block.getState();
                                if (chest.getInventory() instanceof DoubleChestInventory) {
                                    DoubleChestInventory doubleChest = (DoubleChestInventory) chest.getInventory();
                                    Chest leftChest = (Chest) doubleChest.getLeftSide().getHolder();
                                    Chest rightChest = (Chest) doubleChest.getRightSide().getHolder();

                                    // If the partner chest is not in the list of blocks to be destroyed, add it
                                    if (!event.blockList().contains(leftChest.getBlock())) {
                                        additionalBlocksToRegen.add(leftChest.getBlock());
                                    }
                                    if (!event.blockList().contains(rightChest.getBlock())) {
                                        additionalBlocksToRegen.add(rightChest.getBlock());
                                    }
                                }
                            }

                            if (RaidToolsUtils.isBlockToRemove(block.getType()) && block.getType() != Material.AIR) {
                                additionalBlocksToRegen.add(block);
                            } else {
                                if (tnt.hasMetadata("NoDestroy")) {
                                    blocksToNotDestroy.add(block);
                                }
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
    public void TeleportShardChatListener(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        // Check if the player is in a teleport process
        if (dataAccessManager.getItemDao().playerExistsInTeleportRequests(playerName) && !dataAccessManager.getItemDao().getTeleportRequest(playerName).waitingForDecision()) {
            TeleportRequest request = dataAccessManager.getItemDao().getTeleportRequest(playerName);

            // Cancel the chat event to prevent the typed message from being sent in the chat
            event.setCancelled(true);

            // Process the typed name and handle teleportation
            String targetName = event.getMessage();
            request.sendTeleportationRequest(targetName);
        }
    }

    public boolean isLocationBelow(Location location1, Location location2) {
        // Check if location1 is below location2
        return location2.getY() < location1.getY();
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
