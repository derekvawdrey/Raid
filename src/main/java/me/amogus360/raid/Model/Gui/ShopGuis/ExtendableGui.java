package me.amogus360.raid.Model.Gui.ShopGuis;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.Items.Defending.SummonSkeletonDefender;
import me.amogus360.raid.Model.Items.ItemHandler;
import me.amogus360.raid.Model.Items.Misc.TeleportationShard;
import me.amogus360.raid.Model.Items.Raiding.TntJumperHandler;
import me.amogus360.raid.Model.Items.Raiding.TntLauncherHandler;
import me.amogus360.raid.Model.Items.Raiding.TntShotgunHandler;
import me.amogus360.raid.Utilities.MoneyUtilities;
import me.amogus360.raid.Utilities.RaidToolsUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class ExtendableGui<T extends ItemHandler> extends Gui {
    protected final Map<String, T> itemHandlers;
    protected final DataAccessManager dataAccessManager;
    protected final boolean addAdditionalItems;


    public ExtendableGui(Player player, String title, int rows, Map<String, T> itemHandlers, DataAccessManager dataAccessManager, boolean addAdditionalItems) {
        super(player, title, "Item Shop", rows);
        this.itemHandlers = itemHandlers;
        this.dataAccessManager = dataAccessManager;
        this.addAdditionalItems = addAdditionalItems;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
 
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerItemMeta = filler.getItemMeta();
        fillerItemMeta.setDisplayName("");
        filler.setItemMeta(fillerItemMeta);
        fillGui(filler);
        int i = 0;

        for (ItemHandler weaponHandler : itemHandlers.values()) {
            ItemStack weaponItem = RaidToolsUtils.returnItemStackForWeaponHandler(dataAccessManager, weaponHandler);
            RaidToolsUtils.addPriceLore(weaponItem, weaponHandler.getItemPrice());
            Icon icon = new Icon(weaponItem);
            int finalI = i;
            icon.onClick(clickEvent -> {
                clickEvent.setCancelled(true);
                processPurchase(weaponItem, weaponHandler.getItemPrice(), "Purchased");
            });
            addItem(finalI, icon);
            i++;
        }
        if(addAdditionalItems) {
            addAdditionalItems(i);
        }

    }

    public void addAdditionalItems(int i) {

    }

    // Add common methods and functionality here that can be reused by subclasses.
    protected static Map<String, ItemHandler> initalizeRaidItems() {
        Map<String, ItemHandler> weaponHandlers = new HashMap<>();

        TntLauncherHandler tntLauncherHandler = new TntLauncherHandler();
        TntShotgunHandler tntShotgunHandler = new TntShotgunHandler();
        TntJumperHandler tntJumperHandler = new TntJumperHandler();
        // The lore here determines the effect
        weaponHandlers.put(tntJumperHandler.getActivationLore(), tntJumperHandler);
        weaponHandlers.put(tntShotgunHandler.getActivationLore(), tntShotgunHandler);
        weaponHandlers.put(tntLauncherHandler.getActivationLore(), tntLauncherHandler);

        return weaponHandlers;
    }
    protected static Map<String, ItemHandler> initalizeDefenderItems() {
        Map<String, ItemHandler> weaponHandlers = new HashMap<>();

        SummonSkeletonDefender summonSkeletonDefender = new SummonSkeletonDefender();
        // The lore here determines the effect
        weaponHandlers.put(summonSkeletonDefender.getActivationLore(), summonSkeletonDefender);
        return weaponHandlers;
    }
    protected static Map<String, ItemHandler> initalizeMiscItems() {
        Map<String, ItemHandler> weaponHandlers = new HashMap<>();
        TeleportationShard teleportShard = new TeleportationShard();

        weaponHandlers.put(teleportShard.getActivationLore(), teleportShard);
        return weaponHandlers;
    }

    protected boolean processPurchase(ItemStack item, int price, String purchase_message){
        try {
            if(MoneyUtilities.buyItem(dataAccessManager,player.getUniqueId(),price)) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0F, 1.0F);
                RaidToolsUtils.removePriceLore(item);
                player.getInventory().addItem(item);
                MessageManager.sendMessage(player, purchase_message);
                return true;
            }else{
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                MessageManager.sendMessage(player, "Not enough money :(");
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
