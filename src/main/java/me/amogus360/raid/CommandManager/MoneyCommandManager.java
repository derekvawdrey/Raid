package me.amogus360.raid.CommandManager;

import me.amogus360.raid.Commands.Money.AddMoneyCommand;
import me.amogus360.raid.Commands.Money.MoneyCommand;
import me.amogus360.raid.Commands.Money.SendMoneyCommand;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class MoneyCommandManager extends CommandManager {

    public MoneyCommandManager(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        super(plugin, dataAccessManager, "money");
    }

    @Override
    protected void registerSubCommands() {
        // Register your subcommands here
        registerSubCommand("show", new MoneyCommand(plugin, "/money show"));
        registerSubCommand("add", new AddMoneyCommand(plugin, "/money add [player_name] [amount]"));
        registerSubCommand("send", new SendMoneyCommand(plugin, "/money send [player_name] [amount]"));
    }

}
