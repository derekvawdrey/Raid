package me.amogus360.raid.Commands.Faction;

import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DAO.FactionDao;
import me.amogus360.raid.RaidCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class CreateFactionCommand extends RaidCommand {
    private final FactionDao factionDataAccess;
    public CreateFactionCommand(JavaPlugin plugin, String usage, FactionDao factionDataAccess) {
        super(plugin, usage);
        this.factionDataAccess = factionDataAccess;
    }

    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return;
        }

        if(args.length != 1){
            // TODO: Tell the user that there can be no spaces
            this.tellUsage(sender);
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        FactionDao factionDao = new FactionDao(commandManager.returnConnect());
        // Check if the player already has a faction
        if (factionDao.isPlayerInFaction(playerUUID)) {
            player.sendMessage("You are already in a faction.");
        } else {
            // Create a new faction
            factionDao.createFaction(playerUUID, args[1]);
            player.sendMessage("Faction created successfully!");
        }

        return;
    }

}
