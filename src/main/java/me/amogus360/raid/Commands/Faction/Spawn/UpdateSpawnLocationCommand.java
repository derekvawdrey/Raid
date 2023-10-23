package me.amogus360.raid.Commands.Faction.Spawn;

import me.amogus360.raid.CommandManager.CommandManager;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.FactionInfo;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class UpdateSpawnLocationCommand extends RaidCommand {
    public UpdateSpawnLocationCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }
    // TODO: Stop a user from changing the spawn during a raid
    @Override
    public void execute(CommandSender sender, String[] args, CommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender,"This command can only be used by players.");
            return;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        DataAccessManager dataAccessManager = commandManager.getDataAccessManager();
        // Check if the player is in a faction
        FactionInfo factionInfo = dataAccessManager.getFactionDao().getFactionInfoByPlayerUUID(playerUUID);

        if(!commandManager.getDataAccessManager().getFactionDao().isPlayerFactionOwner(player.getUniqueId())
                && !commandManager.getDataAccessManager().getFactionDao().hasFactionTitle(player.getUniqueId(), "officer")){
            MessageManager.sendMessage(player, "You are not an officer or owner of a faction, you can't use this command!");
        }

        if (factionInfo != null && dataAccessManager.getLandClaimDao().isClaimedByFactionId(player.getLocation(), factionInfo.getFactionId())) {
            int factionId = factionInfo.getFactionId();
            Location newRaidBossLocation = player.getLocation();

            // Update the raid boss location in the database
            dataAccessManager.getFactionDao().updateSpawnLocation(factionId, newRaidBossLocation);
            MessageManager.sendMessage(player, "Faction spawn location updated successfully.");
        }else{
            MessageManager.sendMessage(player, "Faction spawn location must be within your faction claim.");
        }

        return;
    }
}