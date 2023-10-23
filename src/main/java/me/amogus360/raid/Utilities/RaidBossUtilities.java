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
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.WITHER_SKELETON);

        entity.setCustomName(name);
        entity.setCustomNameVisible(true);
        entity.setMaxHealth(100);
        entity.setHealth(100);
        entity.setRemoveWhenFarAway(false);

        PotionEffect regenerationEffect = new PotionEffect(PotionEffectType.REGENERATION, 200000000, 1);


        entity.addPotionEffect(regenerationEffect);

        int factionId = faction_id; // Replace with the actual faction ID
        String metadataKey = "faction_id";
        MetadataValue factionMetadata = new FixedMetadataValue(dataAccessManager.getPlugin(), factionId);
        entity.setMetadata(metadataKey, factionMetadata);

        RaidBossBar entityBossBar = new RaidBossBar(entity);
        dataAccessManager.getBossBarDataAccess().addBossBar(entityBossBar);

        Ravager ravager = (Ravager) location.getWorld().spawnEntity(location, EntityType.RAVAGER);
        ravager.setPassenger(entity);
        ravager.setRemoveWhenFarAway(false);

        location.getWorld().playSound(entity.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1.0f, 1.0f);
        location.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 2.0f, 5.0f);
    }
}
