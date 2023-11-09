package me.amogus360.raid.Commands.Faction;
import me.amogus360.raid.CommandManager.CommandManager;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.CommandManager.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class DeleteFactionCommand extends RaidCommand {

    public DeleteFactionCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }

    @Override
    public void execute(CommandSender sender, String[] args, CommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender, "Only players can use this command.");
            return;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        DataAccessManager dataAccessManager = commandManager.getDataAccessManager();

        // Check if the player is not in a faction
        if (!dataAccessManager.getFactionDao().isPlayerInFaction(playerUUID)) {
            MessageManager.sendMessage(player, "You are not currently in a faction.");
            return;
        }

        // Check if the player is the owner of the faction
        if (dataAccessManager.getFactionDao().isPlayerFactionOwner(playerUUID)) {
            int factionId = dataAccessManager.getFactionDao().getFactionIdByPlayerUUID(playerUUID);

            // Check if there is an ongoing raid for the faction
            if (dataAccessManager.getRaidDao().isFactionInOngoingRaid(factionId) || dataAccessManager.getRaidDao().isRaidDeclared(factionId)) {
                MessageManager.sendMessage(player, "You cannot delete the faction while it is involved in a raid.");
            } else {
                // Remove the player from their current faction and delete it
                dataAccessManager.getLandClaimDao().removeAllLandClaimsByFactionId(factionId);
                dataAccessManager.getFactionDao().deleteFaction(playerUUID);
                MessageManager.sendMessage(player, "You have deleted your faction.");
            }
        } else {
            MessageManager.sendMessage(player, "You are not the owner of the faction and can't delete it.");
        }
    }
}
