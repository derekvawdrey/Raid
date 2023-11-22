package me.amogus360.raid.EventHandlers;

import me.amogus360.raid.DataAccessManager;
import me.amogus360.raid.MessageManager;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class RaidBossEventHandler implements Listener {

    private final DataAccessManager dataAccessManager; // Assuming you have a TableManager instance

    public RaidBossEventHandler(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            PersistentDataContainer dataContainer = entity.getPersistentDataContainer();

            // Check if the entity has a faction_id
            if (dataContainer.has(new NamespacedKey(dataAccessManager.getPlugin(), "faction_id"), PersistentDataType.INTEGER)) {
                int entityFactionId = dataContainer.get(new NamespacedKey(dataAccessManager.getPlugin(), "faction_id"), PersistentDataType.INTEGER);

                // Remove boss bar and update data
                dataAccessManager.getBossBarDataAccess().removeAllPlayersFromBossBar(entity.getUniqueId());
                dataAccessManager.getBossBarDataAccess().removeBossBar(entity.getUniqueId());
                dataAccessManager.getRaidDao().updateBossKilled(entityFactionId, true);

                // Send a message
                MessageManager.sendGlobalMessage(dataAccessManager.getPlugin(), entity.getCustomName() + " has been defeated!");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity) || !(event.getDamager() instanceof Player)) {
            return;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();
        Player player = (Player) event.getDamager();

        if (!entity.getPersistentDataContainer().has(new NamespacedKey(dataAccessManager.getPlugin(), "faction_id"), PersistentDataType.INTEGER)) {
            return;
        }

        int entityFactionId = entity.getPersistentDataContainer().get(new NamespacedKey(dataAccessManager.getPlugin(), "faction_id"), PersistentDataType.INTEGER);

        // Get the faction ID of the player.
        int playerFactionId = dataAccessManager.getFactionDao().getFactionIdByPlayerUUID(player.getUniqueId());

        if (entityFactionId == playerFactionId) {
            event.setCancelled(true); // Cancel the damage event if they are in the same faction.
        } else {
            handleNonFriendlyDamage(entity, event);
        }
    }

    private void handleNonFriendlyDamage(LivingEntity entity, EntityDamageByEntityEvent event) {
        if (entity.getPersistentDataContainer().has(new NamespacedKey(dataAccessManager.getPlugin(), "defender"), PersistentDataType.INTEGER)) {
            Location location = entity.getLocation();
            randomSkeletonArmy(location);
            randomZombieHoard(location);
            randomWitchPotionThrow(location);
            event.setCancelled(true);
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
