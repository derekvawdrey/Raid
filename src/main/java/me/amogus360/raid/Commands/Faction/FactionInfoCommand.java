package me.amogus360.raid.Commands.Faction;

import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DAO.FactionDao;
import me.amogus360.raid.Model.FactionInfo;
import me.amogus360.raid.RaidCommandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class FactionInfoCommand extends RaidCommand {
    private final FactionDao factionDataAccess;
    public FactionInfoCommand(JavaPlugin plugin, String usage, FactionDao factionDataAccess) {
        super(plugin, usage);
        this.factionDataAccess = factionDataAccess;
    }
    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        // Check if the player is in a faction
        FactionInfo factionInfo = factionDataAccess.getFactionInfoByPlayerUUID(playerUUID);

        if (factionInfo != null) {
            player.sendMessage("You are a member of the faction '" + factionInfo.getFactionName() + "'.");
            player.sendMessage("Faction Owner: " + factionInfo.getFactionOwnerUUID());
        } else {
            player.sendMessage("You are not currently in a faction.");
        }

        return;
    }
}