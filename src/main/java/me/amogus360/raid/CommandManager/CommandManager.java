package me.amogus360.raid.CommandManager;

import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class CommandManager implements CommandExecutor {

    protected final JavaPlugin plugin;
    protected final DataAccessManager dataAccessManager;
    protected final Map<String, RaidCommand> subCommands;
    protected final String baseCommand;

    public CommandManager(JavaPlugin plugin, DataAccessManager dataAccessManager, String baseCommand) {
        this.plugin = plugin;
        this.dataAccessManager = dataAccessManager;
        this.subCommands = new HashMap<>();
        this.baseCommand = baseCommand;
        registerSubCommands();
    }

    protected void registerSubCommands() {
        // Register your subcommands in subclasses.
    }

    protected void registerSubCommand(String name, RaidCommand subCommand) {
        subCommands.put(name.toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Display a help message or handle it as needed
            listAvailableSubcommands(sender, "");
            return true;
        }

        StringBuilder subCommandName = new StringBuilder(args[0].toLowerCase());
        RaidCommand subCommand = subCommands.get(subCommandName.toString());
        if (subCommand != null) {
            // Execute the subcommand and remove the used args
            subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length), this);
            return true;
        }



        for (int i = 1; i < args.length; i++) {
            subCommandName.append(" ").append(args[i].toLowerCase());
            subCommand = subCommands.get(subCommandName.toString());
            if (subCommand != null) {
                // Execute the subcommand and remove the used args
                subCommand.execute(sender, Arrays.copyOfRange(args, i, args.length), this);
                return true;
            }
        }

        // Unknown subcommand
        listAvailableSubcommands(sender, subCommandName.toString());
        return true;
    }

    private void listAvailableSubcommands(CommandSender sender, String subcommandPrefix) {
        MessageManager.sendMessage(sender, "Available subcommands for /" + this.baseCommand + " " + subcommandPrefix + ":");

        for (String subCommand : subCommands.keySet()) {
            if (subCommand.startsWith(subcommandPrefix)) {
                MessageManager.sendMessage(sender, "  - /"+ this.baseCommand + " " + subCommand);
            }
        }
    }


    protected String[] removeOneArg(String[] args) {
        if (args.length <= 1) {
            return new String[0];
        }
        String[] result = new String[args.length - 1];
        System.arraycopy(args, 1, result, 0, args.length - 1);
        return result;
    }

    public DataAccessManager getDataAccessManager() {
        return this.dataAccessManager;
    }
}
