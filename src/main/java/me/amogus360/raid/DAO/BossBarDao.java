package me.amogus360.raid.DAO;

import me.amogus360.raid.Model.RaidBossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarDao {
    private Map<UUID, RaidBossBar> bossBars;

    public BossBarDao() {
        this.bossBars = new HashMap<>();
    }

    public void addBossBar(RaidBossBar bossBar) {
        this.bossBars.put(bossBar.getEntityId(), bossBar);
    }

    public RaidBossBar getBossBar(UUID uuid) {
        return this.bossBars.get(uuid);
    }

    public void removeBossBar(UUID id) {
        this.bossBars.remove(id);
    }

    public void removeAllPlayersFromBossBar(UUID uuid) {
        RaidBossBar bossBar = this.bossBars.get(uuid);
        if (bossBar != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                bossBar.removePlayer(player);
            }
        }
    }


    public void updateAll() {
        for (RaidBossBar bossBar : this.bossBars.values()) {
            bossBar.update();
        }
    }
}