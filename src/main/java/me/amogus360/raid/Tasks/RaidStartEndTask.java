package me.amogus360.raid.Tasks;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.RaidInfo;
import me.amogus360.raid.Utilities.RaidBossUtilities;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.List;

public class RaidStartEndTask extends BukkitRunnable {
    private DataAccessManager dataAccessManager;
    private JavaPlugin plugin;

    public RaidStartEndTask(DataAccessManager dataAccessManager, JavaPlugin plugin) {
        this.dataAccessManager = dataAccessManager;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // Get the current time
        LocalDateTime currentTime = LocalDateTime.now();

        // Get ongoing raids
        List<RaidInfo> toStartRaids = dataAccessManager.getRaidDao().getTimeToStartRaids(currentTime);

        for (RaidInfo raid : toStartRaids) {
            // Handle ongoing raids (e.g., update status, check for completion, etc.)
            // You can add your logic here
            // For example, you might want to check if a raid is completed based on some conditions.
            MessageManager.sendGlobalMessage(this.plugin, "The raid on faction " + raid.getDefendingFactionName() + " has started!");
            RaidBossUtilities.createRaidBoss(dataAccessManager, dataAccessManager.getFactionDao().getRaidBossLocation(raid.getDefendingFactionId()), raid.getDefendingFactionId(), ChatColor.GREEN + "Mayor of " + ChatColor.RED + raid.getDefendingFactionName());
            dataAccessManager.getRaidDao().startRaid(raid.getRaidId());
        }

        // Get ended raids
        List<RaidInfo> endedRaids = dataAccessManager.getRaidDao().getEndedRaids(currentTime);

        for (RaidInfo raid : endedRaids) {
            // Handle ended raids (e.g., cleanup, calculate rewards, etc.)
            // You can add your logic here
            MessageManager.sendGlobalMessage(this.plugin, "The raid on faction " + raid.getDefendingFactionName() + " has ended!");
            dataAccessManager.getRaidDao().stopRaid(raid.getRaidId());
        }
    }

    public static void startTask(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        // Run the task periodically (adjust the interval as needed)
        int intervalTicks = 20 * 20; // 60 seconds
        new RaidStartEndTask(dataAccessManager, plugin).runTaskTimer(plugin, 0, intervalTicks);
    }
}
