package me.amogus360.raid.Tasks;

import me.amogus360.raid.DAO.BlockInfoDao;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import me.amogus360.raid.Model.Block.BlockInfo;
import me.amogus360.raid.Utilities.BlockUtilities;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class BlockReplacementTask extends BukkitRunnable {

    private final JavaPlugin plugin; // Store the plugin instance
    private final DataAccessManager dataAccessManager;
    public BlockReplacementTask(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        this.plugin = plugin;
        this.dataAccessManager = dataAccessManager;
    }
    @Override
    public void run() {
        // Get the current timestamp
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        // Retrieve blocks that need to be replaced
        List<BlockInfo> blocksToReplace = dataAccessManager.getBlockInfoDao().getAndDeleteBlocksToReplace(currentTime);
        BlockUtilities.placeBlockArray(blocksToReplace);
        MessageManager.sendGlobalMessage(this.plugin, "Starting block replacement");
    }

    public static void startTask(JavaPlugin plugin, DataAccessManager dataAccessManager) {
        // Run the task every 30 minutes
        BlockReplacementTask cleanupTask = new BlockReplacementTask(plugin, dataAccessManager);
        cleanupTask.runTaskTimer(plugin, 0L, 20L * 60L * 30L);
    }

}
