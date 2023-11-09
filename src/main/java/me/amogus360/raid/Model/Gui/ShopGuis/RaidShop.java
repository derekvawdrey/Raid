package me.amogus360.raid.Model.Gui.ShopGuis;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.Model.Items.ItemHandler;
import me.amogus360.raid.Model.Items.Raiding.TntJumperHandler;
import me.amogus360.raid.Model.Items.Raiding.TntLauncherHandler;
import me.amogus360.raid.Model.Items.Raiding.TntShotgunHandler;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RaidShop extends ExtendableGui<ItemHandler> {
    public RaidShop(Player player, DataAccessManager dataAccessManager) {
        super(player, "Raid Item Shop", 3, initalizeRaidItems(), dataAccessManager);
    } 


}
