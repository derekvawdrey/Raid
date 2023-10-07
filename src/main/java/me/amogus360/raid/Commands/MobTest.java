package me.amogus360.raid.Commands;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.FactionInfo;
import me.amogus360.raid.Model.RaidBossBar;
import me.amogus360.raid.RaidCommandManager;
import me.amogus360.raid.Utilities.RaidBossUtilities;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class MobTest extends RaidCommand {

    public MobTest(JavaPlugin plugin, String usage) {
        super(plugin, usage);
    }

    @Override
    public void execute(CommandSender sender, String[] args, RaidCommandManager commandManager) {
        if (!(sender instanceof Player)) {
            MessageManager.sendMessage(sender,"Only players can use this command.");
            return;
        }

        // Your logic for /raid money here
        Player player = (Player) sender;
        // Spawn a custom-named entity with 1000 health
        FactionInfo factionInfo = commandManager.getDataAccessManager().getFactionDao().getFactionInfoByPlayerUUID(player.getUniqueId());
        RaidBossUtilities.createRaidBoss(commandManager.getDataAccessManager(), player.getLocation(), factionInfo.getFactionId(), ChatColor.RED + "Mayor of " + ChatColor.AQUA + factionInfo.getFactionName());

    }


}
