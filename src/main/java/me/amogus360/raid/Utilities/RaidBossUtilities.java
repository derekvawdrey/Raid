package me.amogus360.raid.Utilities;
import org.bukkit.NamespacedKey;
import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.Model.RaidBossBar;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class RaidBossUtilities {

    public static void createRaidBoss(DataAccessManager dataAccessManager, Location location, int faction_id, String name){

        location.getChunk().load();
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.IRON_GOLEM);

        entity.setCustomName(name);
        entity.setCustomNameVisible(true);
        entity.setMaxHealth(300);
        entity.setHealth(300);
        entity.setRemoveWhenFarAway(false);

        PotionEffect regenerationEffect = new PotionEffect(PotionEffectType.SPEED, 200000000, 2);


        entity.addPotionEffect(regenerationEffect);

        int factionId = faction_id; // Replace with the actual faction ID
        String metadataKey = "faction_id";
        MetadataValue factionMetadata = new FixedMetadataValue(dataAccessManager.getPlugin(), factionId);
        entity.setMetadata(metadataKey, factionMetadata);

        RaidBossBar entityBossBar = new RaidBossBar(entity);
        dataAccessManager.getBossBarDataAccess().addBossBar(entityBossBar);


        location.getWorld().playSound(entity.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1.0f, 1.0f);
        location.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 2.0f, 5.0f);
    }
}
