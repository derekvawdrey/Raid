package me.amogus360.raid.CommandManager;

import me.amogus360.raid.Commands.Raid.MobTest;
import me.amogus360.raid.Commands.Raid.RaidJoin;
import me.amogus360.raid.Commands.Raid.RaidStart;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.plugin.java.JavaPlugin;

public class RaidCommandManager extends CommandManager {


    public RaidCommandManager(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        super(plugin, dataAccessManager);
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
        if(subcommand.equals("mob")) {
            new MobTest(plugin, "/raid mob").execute(sender, newArgs, this);
        }else if(subcommand.equals("start")){
            new RaidStart(plugin, "/raid start [faction_name]").execute(sender, newArgs, this);
        }else if(subcommand.equals("join")){
            new RaidJoin(plugin, "/raid join [faction_name]").execute(sender, newArgs, this);
        }

        else {
            // Unknown subcommand
            MessageManager.sendMessage(sender,"Unknown subcommand: " + subcommand);
        }

        return true;
    }

}
