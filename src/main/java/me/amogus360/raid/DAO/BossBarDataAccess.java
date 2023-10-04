package me.amogus360.raid.DAO;

import me.amogus360.raid.Model.RaidBossBar;
import org.bukkit.entity.LivingEntity;
import java.util.HashMap;
import java.util.Map;

public class BossBarDataAccess {
    private Map<Integer, RaidBossBar> bossBars;

    public BossBarDataAccess() {
        this.bossBars = new HashMap<>();
    }

    public void addBossBar(RaidBossBar bossBar) {
        this.bossBars.put(bossBar.getEntityId(), bossBar);
    }

    public RaidBossBar getBossBar(int id) {
        return this.bossBars.get(id);
    }

    public void removeBossBar(int id) {
        this.bossBars.remove(id);
    }

    public void updateAll() {
        for (RaidBossBar bossBar : this.bossBars.values()) {
            bossBar.update();
        }
    }
}