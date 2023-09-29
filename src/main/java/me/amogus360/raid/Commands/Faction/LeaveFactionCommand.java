package me.amogus360.raid.Commands.Faction;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DAO.FactionDao;
import me.amogus360.raid.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class LeaveFactionCommand extends RaidCommand {

    private final FactionDao factionDao;
    public LeaveFactionCommand(JavaPlugin plugin, String usage, FactionDao factionDataAccess) {
        super(plugin, usage);
        this.factionDao = factionDataAccess;
    }

    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        // TODO: If the player is the last player in the faction, delete the faction
        // TODO: If the player is the owner, disband the faction/ask for clarification before deleting the faction
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        // Check if the player is not in a faction
        if (!factionDao.isPlayerInFaction(playerUUID)) {
            player.sendMessage("You are not currently in a faction.");
            return;
        }

        // Remove the player from their current faction
        factionDao.removePlayerFromFaction(playerUUID);
        player.sendMessage("You have left your faction.");
    }
}
