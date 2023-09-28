package me.amogus360.raid.Commands;

import me.amogus360.raid.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class RaidCommand {

    protected final JavaPlugin plugin;

    public RaidCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void execute(CommandSender sender, String[] args, RaidCommandManager commandManager);

    // You can add common methods or fields here that are shared by all command classes
}
