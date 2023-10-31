package me.amogus360.raid.EventHandlers;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class RaidBossEventHandler implements Listener {

    private final DataAccessManager dataAccessManager; // Assuming you have a TableManager instance

    public RaidBossEventHandler(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            // Remove the boss bar and
            Entity entity = event.getEntity();
            if (entity.hasMetadata("faction_id") && !entity.hasMetadata("defender")) {
                LivingEntity livingEntity = (LivingEntity) event.getEntity();
                if (entity.isDead()) {
                    dataAccessManager.getBossBarDataAccess().removeAllPlayersFromBossBar(entity.getUniqueId());
                    dataAccessManager.getBossBarDataAccess().removeBossBar(entity.getUniqueId());

                    List<MetadataValue> metadata = entity.getMetadata("faction_id");

                    if (!metadata.isEmpty() && metadata.get(0).value() instanceof Integer) {
                        int entityFactionId = metadata.get(0).asInt();
                        dataAccessManager.getRaidDao().updateBossKilled(entityFactionId,true);
                    }

                    MessageManager.sendGlobalMessage(dataAccessManager.getPlugin(), event.getEntity().getCustomName() + " has been defeated!");
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            Entity entity = event.getEntity();
            LivingEntity livingEntity = (LivingEntity) event.getEntity();
            Entity entityDamager = event.getDamager();
            if (entity.hasMetadata("faction_id") && entityDamager instanceof Player) {
                // TODO: Check if the player who damaged the entity was part of the same faction, if so cancel
                Player player = (Player) entityDamager;
                List<MetadataValue> metadata = entity.getMetadata("faction_id");

                if (!metadata.isEmpty() && metadata.get(0).value() instanceof Integer) {
                    int entityFactionId = metadata.get(0).asInt();

                    // Get the faction ID of the player.
                    int playerFactionId = dataAccessManager.getFactionDao().getFactionIdByPlayerUUID(player.getUniqueId());

                    // Compare the faction IDs.
                    if (entityFactionId == playerFactionId) {
                        event.setCancelled(true); // Cancel the damage event if they are in the same faction.
                    }else{
                        if(entity.hasMetadata("defender")){
                            livingEntity.damage(event.getDamage());
                            Location location = entity.getLocation();
                            randomLightningStrike(location);
                            randomEndermanTeleport(location);
                            randomSkeletonArmy(location);
                            randomZombieHoard(location);
                            randomWitchPotionThrow(location);
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    private void randomLightningStrike(Location location){
        double chance = Math.random();
        if (chance <= 0.10) { // 10% chance (0.10 = 10%)
            // Create a lightning strike at a random location around the entity
            double x = location.getX() + (Math.random() - 0.5) * 10; // Random X offset
            double z = location.getZ() + (Math.random() - 0.5) * 10; // Random Z offset
            double y = location.getWorld().getHighestBlockYAt((int) x, (int) z); // Get the highest block's Y-coordinate at the location

            LightningStrike lightningStrike = location.getWorld().strikeLightning(new Location(location.getWorld(), x, y, z));
        }
    }
    public static void randomEndermanTeleport(Location location) {
        double chance = Math.random();
        if (chance <= 0.1) {
            // Create an enderman entity at the given location and make it teleport randomly
            Entity enderman = location.getWorld().spawn(location, Enderman.class);
            ((Enderman) enderman).teleport(location.clone().add(Math.random() * 10 - 5, 0, Math.random() * 10 - 5));
        }
    }

    public static void randomWitchPotionThrow(Location location) {
        double chance = Math.random();
        if (chance <= 0.05) {
            // Create a witch entity at the given location and make it throw a potion
            Entity witch = location.getWorld().spawn(location, Witch.class);
            witch.getWorld().createExplosion(location, 0.0f, false, false, witch);
        }
    }

    public static void randomZombieHoard(Location location) {
        double chance = Math.random();
        if (chance <= 0.05) {
            // Create a group of zombie entities at the given location
            for (int i = 0; i < 5; i++) {
                Entity zombie = location.getWorld().spawn(location, Zombie.class);
            }
        }
    }

    public static void randomSkeletonArmy(Location location) {
        double chance = Math.random();
        if (chance <= 0.05) {
            // Create a group of skeleton entities at the given location
            for (int i = 0; i < 5; i++) {
                Entity skeleton = location.getWorld().spawn(location, Skeleton.class);
            }
        }
    }


}
