package me.amogus360.raid.Commands.Faction;

import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DAO.FactionDao;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.FactionInfo;
import me.amogus360.raid.RaidCommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class FactionInfoCommand extends RaidCommand {
    public FactionInfoCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }
    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender,"This command can only be used by players.");
            return;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        DataAccessManager dataAccessManager = commandManager.getDataAccessManager();
        // Check if the player is in a faction
        FactionInfo factionInfo = dataAccessManager.getFactionDao().getFactionInfoByPlayerUUID(playerUUID);

        if (factionInfo != null) {
            MessageManager.sendMessage(player,"You are a member of the faction '" + factionInfo.getFactionName() + "'.");
            MessageManager.sendMessage(player,"Faction Owner: " + factionInfo.getFactionOwnerUUID());
        } else {
            MessageManager.sendMessage(player,"You are not currently in a faction.");
        }

        return;
    }
}