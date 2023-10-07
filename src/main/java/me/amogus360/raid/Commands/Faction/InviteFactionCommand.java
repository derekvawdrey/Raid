package me.amogus360.raid.Commands.Faction;

import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DAO.FactionDao;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.FactionInfo;
import me.amogus360.raid.RaidCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class InviteFactionCommand extends RaidCommand {

    // Your command method
    public InviteFactionCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }
    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        if (args.length < 1) {
            this.tellUsage(sender);
            return;
        }

        FactionDao factionDao = commandManager.getDataAccessManager().getFactionDao();
        Player inviter = (Player) sender;
        UUID inviterUUID = inviter.getUniqueId();
        FactionInfo faction = factionDao.getFactionInfoByPlayerUUID(inviterUUID);

        if(faction == null){
            MessageManager.sendMessage(inviter, "You are not in a faction!");
            return;
        }

        if (!factionDao.isPlayerFactionOwner(inviterUUID) && !factionDao.hasTitle(inviterUUID, "officer")) {
            MessageManager.sendMessage(inviter, "Only the faction owner or officers can invite players to the faction.");
            return;
        }



        String inviteeName = args[0];
        Player invitee = Bukkit.getPlayer(inviteeName);

        if (invitee == null || !invitee.isOnline()) {
            MessageManager.sendMessage(inviter, "Player '" + inviteeName + "' is not online or doesn't exist.");
            return;
        }

        UUID inviteeUUID = invitee.getUniqueId();

        // Check if the invitee is already in a faction
        if (factionDao.isPlayerInFaction(inviteeUUID)) {
            MessageManager.sendMessage(inviter, "The player is already a member of a faction.");
            return;
        }

        // Send an invitation to the invitee
        factionDao.invitePlayerToJoinFaction(inviterUUID, inviteeUUID, faction.getFactionId());
        MessageManager.sendMessage(inviter, "Invitation sent to the player.");
        MessageManager.sendMessage(invitee, "You have been invited to join the faction: " + faction.getFactionName());
    }
}
