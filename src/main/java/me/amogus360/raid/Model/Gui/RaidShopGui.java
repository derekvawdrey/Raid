package me.amogus360.raid.Model.Gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.Model.RaidTools.Enhancements.TntJumperHandler;
import me.amogus360.raid.Model.RaidTools.Enhancements.TntLauncherHandler;
import me.amogus360.raid.Model.RaidTools.Enhancements.TntShotgunHandler;
import me.amogus360.raid.Model.RaidTools.SpecialItems.SummonSkeletonDefender;
import me.amogus360.raid.Model.RaidTools.WeaponHandler;
import me.amogus360.raid.Utilities.MoneyUtilities;
import me.amogus360.raid.Utilities.RaidToolsUtils;
import me.amogus360.raid.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RaidShopGui extends Gui {
    private final Map<String, WeaponHandler> weaponHandlers;
    private final DataAccessManager dataAccessManager;
    public RaidShopGui(Player player, DataAccessManager dataAccessManager) {
        super(player, "faction-shop-raid", "Raiding Weapon Shop", 3);
        this.weaponHandlers = initializeWeaponHandlers();
        this.dataAccessManager = dataAccessManager;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerItemMeta = filler.getItemMeta();
        fillerItemMeta.setDisplayName("");
        filler.setItemMeta(fillerItemMeta);
        fillGui(filler);
        int i = 0;

        for (WeaponHandler weaponHandler : weaponHandlers.values()) {
            ItemStack weaponItem = RaidToolsUtils.returnItemStackForWeaponHandler(dataAccessManager, weaponHandler);

            Icon icon = new Icon(weaponItem);
            int finalI = i;
            icon.onClick(clickEvent -> {
                clickEvent.setCancelled(true);
                try {
                    if(MoneyUtilities.buyItem(dataAccessManager,player.getUniqueId(),200)) {
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0F, 1.0F);
                        player.getInventory().addItem(weaponItem);
                        MessageManager.sendMessage(player, "You selected the raid weapon: " + weaponHandler.getActivationLore());
                    }else{
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            addItem(finalI, icon);
            i++;
        }


        // Add the base Raiding Weapon Item
        ItemStack goatHorns = new ItemStack(Material.GOAT_HORN);
        ItemMeta goatHornMeta = goatHorns.getItemMeta();
        goatHornMeta.setDisplayName(ChatColor.YELLOW + "Raiding Weapon");
        goatHorns.setItemMeta(goatHornMeta);
        Icon goatHornIcon = new Icon(goatHorns);

        goatHornIcon.onClick(clickEvent -> {
            clickEvent.setCancelled(true);
            try {
                if(MoneyUtilities.buyItem(dataAccessManager,player.getUniqueId(),100)) {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0F, 1.0F);
                    player.getInventory().addItem(goatHorns);
                    MessageManager.sendMessage(player, "By buying an Item Enhancement, you can enhance this weapon with /item enhance");
                }else{
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                    MessageManager.sendMessage(player, "Not enough money :(");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        addItem(i,goatHornIcon);
    }

    private Map<String, WeaponHandler> initializeWeaponHandlers() {
        Map<String, WeaponHandler> weaponHandlers = new HashMap<>();

        TntLauncherHandler tntLauncherHandler = new TntLauncherHandler();
        TntShotgunHandler tntShotgunHandler = new TntShotgunHandler();
        TntJumperHandler tntJumperHandler = new TntJumperHandler();
        SummonSkeletonDefender summonSkeletonDefender = new SummonSkeletonDefender();
        // The lore here determines the effect
        weaponHandlers.put(tntJumperHandler.getActivationLore(), tntJumperHandler);
        weaponHandlers.put(tntShotgunHandler.getActivationLore(), tntShotgunHandler);
        weaponHandlers.put(tntLauncherHandler.getActivationLore(), tntLauncherHandler);
        weaponHandlers.put(summonSkeletonDefender.getActivationLore(), summonSkeletonDefender);
        return weaponHandlers;
    }

}
