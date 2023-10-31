package me.amogus360.raid.CommandManager;

import me.amogus360.raid.Commands.Item.ItemEnhance;
import me.amogus360.raid.Commands.Raid.MobTest;
import me.amogus360.raid.Commands.Raid.RaidJoin;
import me.amogus360.raid.Commands.Item.ItemShop;
import me.amogus360.raid.Commands.Raid.RaidStart;
import me.amogus360.raid.DataAccessManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemCommandManager extends CommandManager {

    public ItemCommandManager(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        super(plugin, dataAccessManager, "item");
    }

    @Override
    protected void registerSubCommands() {
        // Register your subcommands here
        registerSubCommand("shop",new ItemShop(plugin, "/item shop"));
        registerSubCommand("enhance",new ItemEnhance(plugin, "/item enhance"));
    }

}
