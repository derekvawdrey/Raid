package me.amogus360.raid.Commands;
import me.amogus360.raid.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MoneyCommand extends RaidCommand {

    public MoneyCommand(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }

        // Your logic for /raid money here
        Player player = (Player) sender;

        // Example: Get the player's balance
        int balance = getBalance(player);

        sender.sendMessage("Your balance: " + balance);
    }

    // You can define common methods for all commands here
    private int getBalance(Player player) {
        // Your logic to retrieve the player's balance from your data source
        return 0; // Replace with actual logic
    }
}
