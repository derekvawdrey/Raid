package me.amogus360.raid.Commands.Faction.Spawn;

import me.amogus360.raid.CommandManager.CommandManager;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.FactionInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class TeleportSpawnCommand extends RaidCommand {
    public TeleportSpawnCommand(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }
    @Override
    public void execute(CommandSender sender, String[] args, CommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender,"This command can only be used by players.");
            return;
        }
        DataAccessManager dataAccessManager = commandManager.getDataAccessManager();
        Player player = (Player)sender;
        UUID playerUUID = player.getUniqueId();
        FactionInfo factionInfo = dataAccessManager.getFactionDao().getFactionInfoByPlayerUUID(playerUUID);
        if(factionInfo != null){
            MessageManager.sendMessage(sender,"Teleporting to faction spawn");
            player.teleport(dataAccessManager.getFactionDao().getSpawnLocation(factionInfo.getFactionId()));
        }else{
            MessageManager.sendMessage(sender,"You are not in a faction!");
        }
    }
}
