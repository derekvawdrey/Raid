package me.amogus360.raid.Commands.Money;

import me.amogus360.raid.CommandManager.CommandManager;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DAO.PlayerAccountDao;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.CommandManager.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.UUID;

public class SendMoneyCommand extends RaidCommand {

    public SendMoneyCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }

    @Override
    public void execute(CommandSender sender, String[] args, CommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender,"Only players can use this command.");
            return;
        }

        if (args.length != 2) {
            this.tellUsage(sender);
            return;
        }

        Player senderPlayer = (Player) sender;
        String recipientName = args[0];
        DataAccessManager dataAccessManager = commandManager.getDataAccessManager();
        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            MessageManager.sendMessage(sender,"Invalid amount. Please enter a valid number.");
            return;
        }

        if (amount <= 0) {
            MessageManager.sendMessage(sender,"Amount must be a positive number.");
            return;
        }

        Player recipient = this.plugin.getServer().getPlayer(recipientName);

        if (recipient == null) {
            MessageManager.sendMessage(sender,"Player not found.");
            return;
        }

        UUID senderUUID = senderPlayer.getUniqueId();
        UUID recipientUUID = recipient.getUniqueId();

        // Use the transferMoney method to perform the money transfer
        try {
            PlayerAccountDao playerAccountDao = dataAccessManager.getPlayerAccountDao();
            if(playerAccountDao.transferMoney(senderUUID, recipientUUID, amount)){
                MessageManager.sendMessage(sender,"Transferred " + amount + " money to " + recipient.getName() + ".");
                MessageManager.sendMessage(recipient,sender.getName() + " sent you " + amount + " money.");
            }else{
                MessageManager.sendMessage(sender, "Insufficent funds");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MessageManager.sendMessage(sender, "An error occurred while transferring money.");
            return;
        }
    }
}
