package me.amogus360.raid.Commands.Faction;

import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DAO.FactionDao;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ListInvitesCommand extends RaidCommand {

    public ListInvitesCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }

    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        if (!(sender instanceof Player)) {
            // This command should be executed by a player
            return;
        }

        Player player = (Player) sender;
        FactionDao factionDao = commandManager.getDataAccessManager().getFactionDao();

        // Get the player's UUID
        String playerUUID = player.getUniqueId().toString();

        // Retrieve the list of pending invitations for the player
        List<String> invites = factionDao.getPendingInvitations(playerUUID);

        if (invites.isEmpty()) {
            MessageManager.sendMessage(player, "You have no pending invitations.");
        } else {
            MessageManager.sendMessage(player, "Pending Invitations:");
            for (String invite : invites) {
                // You can format and display each invitation as needed
                MessageManager.sendMessage(player, "" + invite);
            }
        }
    }
}
