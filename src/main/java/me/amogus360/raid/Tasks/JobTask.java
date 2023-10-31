package me.amogus360.raid.Tasks;

import me.amogus360.raid.DAO.PlayerAccountDao;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class JobTask {
    private DataAccessManager dataAccessManager;
    private JavaPlugin plugin;

    public JobTask(DataAccessManager dataAccessManager, JavaPlugin plugin) {
        this.dataAccessManager = dataAccessManager;
        this.plugin = plugin;
    }

    public static void startTask(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    // Assuming you have a DataAccessManager instance associated with each player
                    int playerBalance = dataAccessManager.getPlayerAccountDao().getPlayerIdByUUID(player.getUniqueId());
                    try {
                        dataAccessManager.getPlayerAccountDao().updateBalance(player.getUniqueId(),playerBalance + 25);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                MessageManager.sendGlobalMessage(dataAccessManager.getPlugin(), "Job money has been awarded");

            }
        }, 0L, 20L * 60L * 15L); // 20 ticks = 1 second
    }
}
