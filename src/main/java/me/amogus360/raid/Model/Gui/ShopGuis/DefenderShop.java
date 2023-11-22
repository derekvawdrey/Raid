package me.amogus360.raid.Model.Gui.ShopGuis;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.Model.Items.ItemHandler;
import org.bukkit.entity.Player;

public class DefenderShop extends ExtendableGui<ItemHandler> {
    public DefenderShop(Player player, DataAccessManager dataAccessManager) {
        super(player, "Defender Item Shop", 3, initalizeDefenderItems(), dataAccessManager, false);
    }


}
