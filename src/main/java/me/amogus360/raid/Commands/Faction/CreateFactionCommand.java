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

public class CreateFactionCommand extends RaidCommand {

    public CreateFactionCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }

    @Override
    public void execute(CommandSender sender, String[] args, CommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender,"Only players can use this command.");
            return;
        }

        if (args.length != 1) {
            this.tellUsage(sender);
            return;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        DataAccessManager dataAccessManager = commandManager.getDataAccessManager();
        // Check if the player already has a faction
        if (dataAccessManager.getFactionDao().isPlayerInFaction(playerUUID)) {
            MessageManager.sendMessage(player,"You are already in a faction.");
        } else {
            // Check if the faction name already exists
            String factionName = args[0];
            if (dataAccessManager.getFactionDao().isFactionNameTaken(factionName)) {
                MessageManager.sendMessage(player,"A faction with that name already exists.");
            } else {
                // Create a new faction
                if(!dataAccessManager.getLandClaimDao().nearbyClaimedArea(player.getLocation(),6)) {
                    int factionId = dataAccessManager.getFactionDao().createFaction(playerUUID, factionName, player.getLocation());
                    dataAccessManager.getFactionDao().addToFaction(playerUUID,factionName);
                    dataAccessManager.getLandClaimDao().claimLand(factionId,player.getLocation());
                    MessageManager.sendMessage(player, "Faction created successfully!");
                }else{
                    MessageManager.sendMessage(player, "You can't create a faction within or near another faction");
                }
            }
        }
    }
}
