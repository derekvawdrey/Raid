package me.amogus360.raid;

import me.amogus360.raid.Commands.AddMoneyCommand;
import me.amogus360.raid.Commands.MoneyCommand;
import me.amogus360.raid.Commands.SendMoneyCommand;
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
            MessageManager.sendMessage(sender,"Usage: /raid <subcommand>");
            return true;
        }

        // Determine the subcommand and delegate to the appropriate class
        String subcommand = args[0].toLowerCase();
        // Remove the first element (subcommand) from the args array
        String[] newArgs = removeOneArg(args);
        if (subcommand.equals("money")) {
            // Handle /raid money
            if(newArgs.length < 1) new MoneyCommand(plugin, "/raid money").execute(sender, newArgs,this);
            else if(newArgs[0].equals("add")) new AddMoneyCommand(plugin, "/raid money add [player_name] [amount]").execute(sender, removeOneArg(newArgs), this);
            else if(newArgs[0].equals("send")) new SendMoneyCommand(plugin, "/raid money send [player_name] [amount]").execute(sender, removeOneArg(newArgs),this);

        }
        else {
            // Unknown subcommand
            MessageManager.sendMessage(sender,"Unknown subcommand: " + subcommand);
        }

        return true;
    }

    private String[] removeOneArg(String[] args){
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        return newArgs;
    }
}
