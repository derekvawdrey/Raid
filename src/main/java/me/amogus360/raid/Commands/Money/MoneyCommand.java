package me.amogus360.raid.Commands.Money;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DAO.PlayerAccountDao;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MoneyCommand extends RaidCommand {

    public MoneyCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }

    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender,"Only players can use this command.");
            return;
        }

        // Your logic for /raid money here
        Player player = (Player) sender;

        // Example: Get the player's balance

        int balance = 0;
        try {
            balance = commandManager.getPlayerAccountDao().getBalance(player.getUniqueId());
        } catch(Exception e){

        }

        MessageManager.sendMessage(sender, "Your balance: " + balance);
    }


}
