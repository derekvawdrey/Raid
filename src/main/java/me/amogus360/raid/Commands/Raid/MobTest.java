package me.amogus360.raid.Commands.Raid;
import me.amogus360.raid.CommandManager.CommandManager;
import me.amogus360.raid.Commands.RaidCommand;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.FactionInfo;
import me.amogus360.raid.CommandManager.RaidCommandManager;
import me.amogus360.raid.Utilities.DefenderUtilities;
import me.amogus360.raid.Utilities.RaidBossUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MobTest extends RaidCommand {

    public MobTest(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }

    //TODO: for rewarding what do we do if a player (who is a friend with another player) purposefully suffocates their entity so the raiding faction gets the resources
    @Override
    public void execute(CommandSender sender, String[] args, CommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender,"Only players can use this command.");
            return;
        }

        // Your logic for /raid money here
        Player player = (Player) sender;
        // Spawn a custom-named entity with 1000 health
        FactionInfo factionInfo = commandManager.getDataAccessManager().getFactionDao().getFactionInfoByPlayerUUID(player.getUniqueId());
        DefenderUtilities.createDefender(commandManager.getDataAccessManager(), player.getLocation(), factionInfo.getFactionId());

    }


}
