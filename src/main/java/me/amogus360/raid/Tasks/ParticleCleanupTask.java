package me.amogus360.raid.Tasks;

import me.amogus360.raid.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleCleanupTask extends BukkitRunnable {
    private final JavaPlugin plugin; // Store the plugin instance

    public ParticleCleanupTask(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // Remove all particles
        for (Effect effect : Effect.values()) {
            this.plugin.getServer().getWorlds().forEach(world -> world.playEffect(world.getSpawnLocation(), effect, 0));
        }
        MessageManager.sendGlobalMessage(this.plugin, "Cleaning up all particles");
    }

    public static void startTask(JavaPlugin plugin) {
        // Run the task every 30 minutes
        ParticleCleanupTask cleanupTask = new ParticleCleanupTask(plugin);
        cleanupTask.runTaskTimer(plugin, 0L, 20L * 60L * 30L);
    }
}
