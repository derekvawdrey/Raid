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

        // TODO (NOT YET): If the faction is disbanded, end the raid without giving awards? and delete the raid boss
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender,"Only players can use this command.");
            return;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        DataAccessManager dataAccessManager = commandManager.getDataAccessManager();
        // Check if the player is not in a faction
        if (!dataAccessManager.getFactionDao().isPlayerInFaction(playerUUID)) {
            MessageManager.sendMessage(player,"You are not currently in a faction.");
            return;
        }

        if(dataAccessManager.getFactionDao().isPlayerFactionOwner(playerUUID)) {
            // Remove the player from their current faction
            dataAccessManager.getLandClaimDao().removeAllLandClaimsByFactionId(dataAccessManager.getFactionDao().getFactionIdByPlayerUUID(playerUUID));
            dataAccessManager.getFactionDao().deleteFaction(playerUUID);

            MessageManager.sendMessage(player, "You have deleted your faction.");
            return;
        }else{
            MessageManager.sendMessage(player, "You are not the owner of the faction, and can't delete it.");
            return;
        }
    }
}
