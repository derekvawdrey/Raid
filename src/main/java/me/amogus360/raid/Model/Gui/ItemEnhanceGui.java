package me.amogus360.raid.Model.Gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.advancedslot.AdvancedSlot;
import mc.obliviate.inventory.advancedslot.AdvancedSlotManager;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.Utilities.RaidToolsUtils;
import me.amogus360.raid.Model.ItemGlow;
import me.amogus360.raid.Model.LoreLevelInformation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemEnhanceGui extends Gui {
    private final DataAccessManager dataAccessManager;
    public ItemEnhanceGui(Player player, DataAccessManager dataAccessManager) {
        super(player, "enhancement", "Enhancement Menu", 6);
        this.dataAccessManager = dataAccessManager;
    }

    AdvancedSlot advancedSlot1;
    AdvancedSlot advancedSlot2;

    AdvancedSlotManager advancedSlotManager = new AdvancedSlotManager(this);

    @Override
    public void onOpen(InventoryOpenEvent event) {
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerItemMeta = filler.getItemMeta();
        fillerItemMeta.setDisplayName("");
        filler.setItemMeta(fillerItemMeta);
        fillGui(filler);
        ItemStack enhancementMaterial = new ItemStack(Material.BARRIER); // Create an ItemStack with your desired material
        ItemMeta itemMeta = enhancementMaterial.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "Enhancement Material");
        enhancementMaterial.setItemMeta(itemMeta);
        Icon enhancementMaterialIcon = new Icon(enhancementMaterial);

        ItemStack itemToBeEnhanced = new ItemStack(Material.BARRIER); // Create an ItemStack with your desired material
        ItemMeta itemToBeEnhancedItemMeta = itemToBeEnhanced.getItemMeta();
        itemToBeEnhancedItemMeta.setDisplayName(ChatColor.YELLOW + "Item To Be Enhanced");
        itemToBeEnhanced.setItemMeta(itemToBeEnhancedItemMeta);
        Icon itemToBeEnhancedIcon = new Icon(itemToBeEnhanced);

        advancedSlot2 = advancedSlotManager.addAdvancedIcon(12, itemToBeEnhancedIcon);

        advancedSlot2.onPrePutClick((e, item) -> {
            if (!item.getType().equals(Material.GOAT_HORN)) {
                player.sendMessage("You cannot put items here except Goat Horn.");

                return true;
            }
            return false;
        });


        advancedSlot1 = advancedSlotManager.addAdvancedIcon(14, enhancementMaterialIcon);

        advancedSlot1.onPrePutClick((e, item) -> {
            if (!item.getType().equals(Material.QUARTZ)) {
                if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
                    return true;
                }
            }
            return false;
        });

        advancedSlot1.onPut((e, item) ->{
            updateConfirmCancelButtons();
        });
        advancedSlot2.onPut((e, item) ->{
            updateConfirmCancelButtons();
        });
        advancedSlot1.onPickup((e,item)->{
            updateConfirmCancelButtons();
        });
        advancedSlot2.onPickup((e,item)->{
            updateConfirmCancelButtons();
        });

        updateConfirmCancelButtons();



    }

    /**
     *
     * This function returns an itemstack with the correct lore
     *
     * @return
     */
    private ItemStack getLoreToItemstack(){
        final Inventory inventory = getInventory();
        // Get items from slots 12 and 14
        ItemStack itemOnSlot14 = inventory.getItem(14);
        ItemStack itemOnSlot12 = inventory.getItem(12).clone();

        // Check if both items are not barriers

            // Copy the lore from item 14 to item 12, removing duplicates and stacking lore levels
            if (itemOnSlot14.hasItemMeta() && itemOnSlot14.getItemMeta().hasLore()) {
                ItemMeta meta12 = itemOnSlot12.getItemMeta();
                List<String> lore12 = new ArrayList<>();

                if (meta12.hasLore()) {
                    lore12 = meta12.getLore();
                }
                List<String> lore14 = itemOnSlot14.getItemMeta().getLore();

                for (String line : lore14) {
                    LoreLevelInformation loreInfo = RaidToolsUtils.parseLoreLine(line);
                    if (loreInfo != null) {
                        boolean added = false;
                        for (int i = 0; i < lore12.size(); i++) {
                            String existingLine = lore12.get(i);
                            LoreLevelInformation existingInfo = RaidToolsUtils.parseLoreLine(existingLine);

                            if (existingInfo != null && existingInfo.getEnhancementType().equals(loreInfo.getEnhancementType())) {
                                int newLevel = existingInfo.getLevel() + loreInfo.getLevel();
                                String newLore = RaidToolsUtils.createLoreString(loreInfo.getEnhancementType(), newLevel);
                                lore12.set(i, newLore);
                                added = true;
                                break;
                            }
                        }
                        if (!added) {
                            lore12.add(line);
                        }
                    }
                }
                meta12.setLore(lore12);
                NamespacedKey key = new NamespacedKey(dataAccessManager.getPlugin(), dataAccessManager.getPlugin().getDescription().getName());
                ItemGlow glow = new ItemGlow(key);
                meta12.addEnchant(glow, 1, true);
                itemOnSlot12.setItemMeta(meta12);
            }

        return itemOnSlot12;
    }

    /**
     * Purpose of this method,
     * remove one lapis from the 2. slot -if exist-
     * enchant item on 1. slot
     */
    private void enchantItem() {
        final Inventory inventory = getInventory();
        // Get items from slots 12 and 14
        ItemStack itemOnSlot14 = inventory.getItem(14);
        ItemStack itemOnSlot12 = getLoreToItemstack();
        if (itemOnSlot12.getType() != Material.BARRIER && itemOnSlot14.getType() != Material.BARRIER) {
            inventory.setItem(12,itemOnSlot12);
            if (itemOnSlot14.getAmount() == 1) {
                advancedSlot1.reset();
            } else {
                itemOnSlot14.setAmount(itemOnSlot14.getAmount() - 1);
            }
            // Update the items in the player's inventory
            updateConfirmCancelButtons();
            player.updateInventory();
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
        }
    }

    private void updateConfirmCancelButtons(){
        final Inventory inventory = getInventory();
        ItemStack itemOnSlot14 = inventory.getItem(14);
        ItemStack itemOnSlot12 = getLoreToItemstack();
        ItemStack confirmIconItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        if (itemOnSlot12.getType() != Material.BARRIER && itemOnSlot14.getType() != Material.BARRIER) {
            confirmIconItem = itemOnSlot12;
        }
        // Create Confirm and Cancel icons

        ItemMeta confirmIconMeta = confirmIconItem.getItemMeta();
        confirmIconMeta.setDisplayName(ChatColor.BOLD + "" + ChatColor.GREEN + "Confirm");
        confirmIconItem.setItemMeta(confirmIconMeta);
        Icon confirmIcon = new Icon(confirmIconItem);

        ItemStack cancelIconItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelIconMeta = cancelIconItem.getItemMeta();
        cancelIconMeta.setDisplayName(ChatColor.RED + "Cancel");
        cancelIconItem.setItemMeta(cancelIconMeta);
        Icon cancelIcon = new Icon(cancelIconItem);

        addItem(12 + 9*2, confirmIcon);
        addItem(14+9*2, cancelIcon);


        confirmIcon.onClick((e) ->{
            enchantItem();
            e.setCancelled(true);
        });

        cancelIcon.onClick((e) ->{
            this.setClosed(true);
            e.setCancelled(true);
        });


    }


}
