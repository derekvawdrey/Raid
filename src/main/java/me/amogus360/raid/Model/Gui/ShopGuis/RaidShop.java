package me.amogus360.raid.Model.Gui.ShopGuis;

import mc.obliviate.inventory.Icon;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.Model.Items.ItemHandler;
import me.amogus360.raid.Model.Items.Raiding.TntJumperHandler;
import me.amogus360.raid.Model.Items.Raiding.TntLauncherHandler;
import me.amogus360.raid.Model.Items.Raiding.TntShotgunHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class RaidShop extends ExtendableGui<ItemHandler> {
    public RaidShop(Player player, DataAccessManager dataAccessManager) {
        super(player, "Raid Item Shop", 3, initalizeRaidItems(), dataAccessManager, true);
    } 

    @Override
    public void addAdditionalItems(int i){
        // Add the goat horn labeled "Raid Tool"
        ItemStack goatHorn = new ItemStack(Material.GOAT_HORN);
        ItemMeta goatHornMeta = goatHorn.getItemMeta();
        goatHornMeta.setDisplayName("Raid Tool");
        goatHorn.setItemMeta(goatHornMeta);
        Icon goatHornIcon = new Icon(goatHorn);
        goatHornIcon.onClick(clickEvent -> {
            clickEvent.setCancelled(true);
            // Add the logic for the goat horn click event here
            processPurchase(goatHorn, 200, "Purchased, use /item enhance to add an enhancement to this tool!");
        });
        addItem(i, goatHornIcon);
    }

}
