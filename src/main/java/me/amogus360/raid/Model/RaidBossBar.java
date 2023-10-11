package me.amogus360.raid.Model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RaidBossBar {
    private LivingEntity entity;
    private BossBar bossBar;
    private UUID entityId;

    public RaidBossBar(LivingEntity entity) {
        this.entity = entity;
        this.bossBar = Bukkit.createBossBar(entity.getCustomName(), BarColor.RED, BarStyle.SOLID);
        this.bossBar.setProgress(entity.getHealth() / entity.getMaxHealth());
        this.entityId = entity.getUniqueId();
    }

    public void update() {
        this.bossBar.setProgress(entity.getHealth() / entity.getMaxHealth());

        for (Player player : Bukkit.getOnlinePlayers()) {
            if(player.getLocation().getWorld() != entity.getLocation().getWorld()) {
                removePlayer(player);
            }else {
                Location playerLocation = player.getLocation();
                if (playerLocation.distance(entity.getLocation()) <= 50) {
                    addPlayer(player);
                } else {
                    removePlayer(player);
                }
            }
        }
    }

    public void addPlayer(Player player) {
        this.bossBar.addPlayer(player);
    }

    public void removePlayer(Player player) {
        this.bossBar.removePlayer(player);
    }
    public UUID getEntityId(){ return this.entityId; }
}
