package me.amogus360.raid.Commands.Faction;

import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DAO.FactionDao;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class CreateFactionCommand extends RaidCommand {
    private final FactionDao factionDao;

    public CreateFactionCommand(JavaPlugin plugin, String usage, FactionDao factionDataAccess) {
        super(plugin, usage);
        this.factionDao = factionDataAccess;
    }

    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender,"Only players can use this command.");
            return;
        }

        if (args.length != 1) {
            MessageManager.sendMessage(sender,"Usage: /createfaction <faction_name>");
            return;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        // Check if the player already has a faction
        if (factionDao.isPlayerInFaction(playerUUID)) {
            MessageManager.sendMessage(player,"You are already in a faction.");
        } else {
            // Check if the faction name already exists
            String factionName = args[0];
            if (factionDao.isFactionNameTaken(factionName)) {
                MessageManager.sendMessage(player,"A faction with that name already exists.");
            } else {
                // Create a new faction
                factionDao.createFaction(playerUUID, factionName);
                MessageManager.sendMessage(player,"Faction created successfully!");
            }
        }
    }
}
