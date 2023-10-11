package me.amogus360.raid.CommandManager;

import me.amogus360.raid.DataAccessManager;
import org.bukkit.command.CommandExecutor;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class CommandManager implements CommandExecutor {

    protected final JavaPlugin plugin;
    protected final DataAccessManager dataAccessManager;

    public CommandManager(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        this.plugin = plugin;
        this.dataAccessManager = dataAccessManager;
    }

    // You can add abstract methods or common functionality here for all extending classes.

    protected String[] removeOneArg(String[] args){
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        return newArgs;
    }

    public DataAccessManager getDataAccessManager(){
        return this.dataAccessManager;
    }
}
