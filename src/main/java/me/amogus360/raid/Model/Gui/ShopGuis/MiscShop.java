package me.amogus360.raid.Model.Gui.ShopGuis;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.Model.Items.ItemHandler;
import org.bukkit.entity.Player;

public class MiscShop extends ExtendableGui<ItemHandler> {
    public MiscShop(Player player, DataAccessManager dataAccessManager) {
        super(player, "Misc Item Shop", 3, initalizeMiscItems(), dataAccessManager, false);
    }
}