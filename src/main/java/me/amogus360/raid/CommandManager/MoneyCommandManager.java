package me.amogus360.raid.CommandManager;

import me.amogus360.raid.Commands.Money.AddMoneyCommand;
import me.amogus360.raid.Commands.Money.MoneyCommand;
import me.amogus360.raid.Commands.Money.SendMoneyCommand;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class MoneyCommandManager extends CommandManager {

    public MoneyCommandManager(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        super(plugin, dataAccessManager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Determine the subcommand and delegate to the appropriate class
        if (args.length > 0) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("add") && args.length > 1) {
                new AddMoneyCommand(plugin, "/money add [player_name] [amount]").execute(sender, removeOneArg(args), this);
            } else if (subcommand.equals("send") && args.length > 1) {
                new SendMoneyCommand(plugin, "/money send [player_name] [amount]").execute(sender, removeOneArg(args), this);
            } else {
                // Unknown subcommand
                MessageManager.sendMessage(sender, "Unknown subcommand: " + subcommand);
            }
        } else {
            new MoneyCommand(plugin, "/money").execute(sender, args, this);
        }

        return true;
    }
}
