package me.amogus360.raid;

import me.amogus360.raid.Commands.AddMoneyCommand;
import me.amogus360.raid.Commands.MoneyCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;

public class RaidCommandManager implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Connection connection;

    public RaidCommandManager(JavaPlugin plugin, Connection connection) {
        this.plugin = plugin;
        this.connection = connection;
    }

    public Connection returnConnect(){
        return this.connection;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Display a help message or handle it as needed
            sender.sendMessage("Usage: /raid <subcommand>");
            return true;
        }

        // Determine the subcommand and delegate to the appropriate class
        String subcommand = args[0].toLowerCase();

        if (subcommand.equals("addmoney")) {
            // Handle /raid addmoney
            new AddMoneyCommand(plugin).execute(sender, args, this);
        } else if (subcommand.equals("money")) {
            // Handle /raid money
            new MoneyCommand(plugin).execute(sender, args,this);
        } else {
            // Unknown subcommand
            sender.sendMessage("Unknown subcommand: " + subcommand);
        }

        return true;
    }
}
