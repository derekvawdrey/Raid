package me.amogus360.raid.Commands.Faction.Invite;

import me.amogus360.raid.CommandManager.CommandManager;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DAO.FactionDao;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.FactionInfo;
import me.amogus360.raid.CommandManager.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class AcceptInvitationCommand extends RaidCommand {

    public AcceptInvitationCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }

    @Override
    public void execute(CommandSender sender, String[] args, CommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender, "Only players can accept invitations.");
            return;
        }

        Player invitee = (Player) sender;
        UUID inviteeUUID = invitee.getUniqueId();

        if (args.length < 1) {
            this.tellUsage(sender);
            return;
        }

        String factionName = args[0];

        FactionDao factionDao = commandManager.getDataAccessManager().getFactionDao();
        if(factionDao.getFactionInfoByPlayerUUID(inviteeUUID) != null) {
            MessageManager.sendMessage(invitee, "You are already in a faction!");
            return;
        }
        FactionInfo faction = factionDao.getFactionInfoByName(factionName);
        if (faction == null) {
            MessageManager.sendMessage(invitee, "The faction '" + factionName + "' does not exist.");
            return;
        }

        if (!factionDao.hasPendingInvitation(inviteeUUID, faction.getFactionId())) {
            MessageManager.sendMessage(invitee, "You do not have a pending invitation to this faction.");
            return;
        }

        factionDao.acceptInvite(inviteeUUID, faction.getFactionId());
        MessageManager.sendMessage(invitee, "You have accepted the invitation to join the faction: " + factionName);
    }
}
