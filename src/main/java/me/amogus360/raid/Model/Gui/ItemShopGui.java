package me.amogus360.raid.Model.Gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.Model.Gui.ShopGuis.DefenderShop;
import me.amogus360.raid.Model.Gui.ShopGuis.MiscShop;
import me.amogus360.raid.Model.Gui.ShopGuis.RaidShop;
import me.amogus360.raid.Model.Items.Raiding.TntJumperHandler;
import me.amogus360.raid.Model.Items.Raiding.TntLauncherHandler;
import me.amogus360.raid.Model.Items.Raiding.TntShotgunHandler;
import me.amogus360.raid.Model.Items.Defending.SummonSkeletonDefender;
import me.amogus360.raid.Model.Items.ItemHandler;
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

public class ItemShopGui extends Gui {
    private final DataAccessManager dataAccessManager;

    public ItemShopGui(Player player, DataAccessManager dataAccessManager) {
        super(player, "faction-shop-raid", "Item Shop", 3);
        this.dataAccessManager = dataAccessManager;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerItemMeta = filler.getItemMeta();
        fillerItemMeta.setDisplayName("");
        filler.setItemMeta(fillerItemMeta);
        fillGui(filler);

        // Add Raids
        ItemStack raidsItem = new ItemStack(Material.TNT);
        ItemMeta raidsItemMeta = raidsItem.getItemMeta();
        raidsItemMeta.setDisplayName(ChatColor.RED + "Raids");
        raidsItem.setItemMeta(raidsItemMeta);
        Icon raidsIcon = new Icon(raidsItem);
        raidsIcon.onClick(clickEvent -> {
            // Handle click for Raids
            clickEvent.setCancelled(true);
            this.setClosed(true);
            new RaidShop(player,dataAccessManager).open();
            // Add your code for Raids here
        });
        addItem(11, raidsIcon); // Centered item

        // Add Defenders
        ItemStack defendersItem = new ItemStack(Material.SHIELD);
        ItemMeta defendersItemMeta = defendersItem.getItemMeta();
        defendersItemMeta.setDisplayName(ChatColor.YELLOW + "Defenders");
        defendersItem.setItemMeta(defendersItemMeta);
        Icon defendersIcon = new Icon(defendersItem);
        defendersIcon.onClick(clickEvent -> {
            // Handle click for Defenders
            clickEvent.setCancelled(true);
            this.setClosed(true);
            new DefenderShop(player,dataAccessManager).open();
            // Add your code for Defenders here
        });
        addItem(13, defendersIcon); // Centered item

        // Add Misc
        ItemStack miscItem = new ItemStack(Material.CHEST);
        ItemMeta miscItemMeta = miscItem.getItemMeta();
        miscItemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Misc");
        miscItem.setItemMeta(miscItemMeta);
        Icon miscIcon = new Icon(miscItem);
        miscIcon.onClick(clickEvent -> {
            // Handle click for Misc
            clickEvent.setCancelled(true);
            this.setClosed(true);
            new MiscShop(player,dataAccessManager).open();
            // Add your code for Misc here
        });
        addItem(15, miscIcon); // Centered item
    }
}
