package me.amogus360.raid.Commands;

import me.amogus360.raid.CommandManager.CommandManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.CommandManager.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class RaidCommand {

    protected final JavaPlugin plugin;
    protected final String usage;

    public RaidCommand(JavaPlugin plugin, String usage) {
        this.plugin = plugin;
        this.usage = usage;
    }

    public abstract void execute(CommandSender sender, String[] args, CommandManager commandManager);
    public void tellUsage(CommandSender sender){
        MessageManager.sendMessage(sender,"Usage: " + this.usage);
    }
    // You can add common methods or fields here that are shared by all command classes
}
