package me.amogus360.raid.Utilities;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.Model.RaidBossBar;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DefenderUtilities {
    public static int defenderProtectionRadius = 12;
    public static void createDefender(DataAccessManager dataAccessManager, Location location, int faction_id){

        location.getChunk().load();
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.ALLAY);

        entity.setCustomName(ChatColor.BOLD + "" + ChatColor.GREEN + "Defender");
        entity.setCustomNameVisible(true);
        entity.setMaxHealth(200);
        entity.setHealth(200);
        entity.setRemoveWhenFarAway(false);

        entity.setGravity(false);
        entity.setAI(false);
        entity.setCollidable(false);

        PotionEffect regenerationEffect = new PotionEffect(PotionEffectType.REGENERATION, 200000000, 2);


        entity.addPotionEffect(regenerationEffect);

        int factionId = faction_id; // Replace with the actual faction ID
        String metadataKey = "faction_id";
        MetadataValue factionMetadata = new FixedMetadataValue(dataAccessManager.getPlugin(), factionId);
        entity.setMetadata(metadataKey, factionMetadata);
        entity.setMetadata("defender", factionMetadata);

        location.getWorld().playSound(entity.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_7, 1.0f, 1.0f);
        location.getWorld().playSound(entity.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 2.0f, 5.0f);
    }
}
