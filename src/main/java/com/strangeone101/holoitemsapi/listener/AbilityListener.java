package com.strangeone101.holoitems.listener;

import com.strangeone101.holoitems.ItemAbility;
import com.strangeone101.holoitems.abilities.RushiaShieldAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class AbilityListener implements Listener {

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            if (ItemAbility.isAbilityActive((Player) event.getTarget(), RushiaShieldAbility.class)) {
                RushiaShieldAbility ability = ItemAbility.getAbility((Player) event.getTarget(), RushiaShieldAbility.class);
                event.setTarget(ability.getShieldMob()); //Target the shield mob instead
            }
        }

        if (RushiaShieldAbility.getShieldMobs().contains(event.getEntity())) {
            event.setCancelled(true); //Stop the shield mob targeting anything
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (RushiaShieldAbility.getShieldMobs().contains(event.getEntity())) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }

    @EventHandler
    public void onInteract(EntityInteractEvent event) {
        if (RushiaShieldAbility.getShieldMobs().contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
