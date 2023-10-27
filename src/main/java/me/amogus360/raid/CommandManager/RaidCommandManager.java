package me.amogus360.raid.CommandManager;

import me.amogus360.raid.Commands.Raid.MobTest;
import me.amogus360.raid.Commands.Raid.RaidJoin;
import me.amogus360.raid.Commands.Raid.RaidStart;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class RaidCommandManager extends CommandManager {

    public RaidCommandManager(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        super(plugin, dataAccessManager, "raid");
    }

    @Override
    protected void registerSubCommands() {
        // Register your subcommands here
        registerSubCommand("mob", new MobTest(plugin, "/raid mob"));
        registerSubCommand("start", new RaidStart(plugin, "/raid start [faction_name]"));
        registerSubCommand("join", new RaidJoin(plugin, "/raid join [faction_name]"));
    }

}
