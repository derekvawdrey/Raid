package me.amogus360.raid.Tasks;

import me.amogus360.raid.DAO.BossBarDataAccess;
import me.amogus360.raid.DataAccessManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BossBarUpdateTask {
    private DataAccessManager dataAccessManager;
    private JavaPlugin plugin;

    public BossBarUpdateTask(DataAccessManager dataAccessManager, JavaPlugin plugin) {
        this.dataAccessManager = dataAccessManager;
        this.plugin = plugin;
    }

    public static void startTask(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                dataAccessManager.getBossBarDataAccess().updateAll();
            }
        }, 0L, 20L); // 20 ticks = 1 second
    }
}
