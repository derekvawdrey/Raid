package me.amogus360.raid.Commands.NoCommandManager;

import me.amogus360.raid.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SetSpawnCommand implements CommandExecutor {

    private final JavaPlugin plugin; // You need to pass the plugin instance to the constructor.

    public SetSpawnCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            MessageManager.sendMessage(commandSender, "Only players can use this command.");
            return false; // Return false to indicate that the command did not execute successfully.
        }

        if(!commandSender.hasPermission("factionsrevived.setspawn")) {
            MessageManager.sendMessage(commandSender, "You don't have permission to run this command");
            return true;
        }else{
            MessageManager.sendMessage(commandSender, "Spawn has been set!");
            Player player = (Player) commandSender;
            plugin.getConfig().set("spawnLocation", player.getLocation());
            plugin.saveConfig();
        }
        return true;

    }
}
