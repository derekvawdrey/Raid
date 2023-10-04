package me.amogus360.raid.Commands.Faction;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DAO.FactionDao;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class LeaveFactionCommand extends RaidCommand {

    public LeaveFactionCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }

    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        // TODO: If the player is the last player in the faction, delete the faction
        // TODO: If the player is the owner, disband the faction/ask for clarification before deleting the faction
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

        // Remove the player from their current faction
        dataAccessManager.getFactionDao().removePlayerFromFaction(playerUUID);
        MessageManager.sendMessage(player,"You have left your faction.");
    }
}
