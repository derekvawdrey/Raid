package me.amogus360.raid.EventHandlers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class RaidBossEventHandler implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // Remove the boss bar and
        Entity entity = event.getEntity();
        LivingEntity livingEntity = (LivingEntity) event.getEntity();

    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        Entity entity = event.getEntity();
        LivingEntity livingEntity = (LivingEntity) event.getEntity();

    }

}
