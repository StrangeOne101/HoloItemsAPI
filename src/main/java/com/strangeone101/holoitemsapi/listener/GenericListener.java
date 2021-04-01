package com.strangeone101.holoitemsapi.listener;

import com.strangeone101.holoitemsapi.Config;
import com.strangeone101.holoitemsapi.event.CIDamageEntityEvent;
import com.strangeone101.holoitemsapi.event.CIEntityDamageEntityEvent;
import com.strangeone101.holoitemsapi.util.CustomDamageSource;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class GenericListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getLastDamageCause() instanceof CIDamageEntityEvent) {
            CustomDamageSource source = ((CIDamageEntityEvent) event.getEntity().getLastDamageCause()).getDamageSource();

            String player = event.getEntity().getName();
            String killer = event.getEntity().getName();

            String end = "Other";

            if (event.getEntity().getLastDamageCause() instanceof CIEntityDamageEntityEvent) {
                killer = ((CIEntityDamageEntityEvent) event.getEntity().getLastDamageCause()).getDamagee().getName();

                //If the damage was done to themselves, use the "Self" message. Else use "Player"
                end = ((CIEntityDamageEntityEvent) event.getEntity().getLastDamageCause()).getDamagee() == event.getEntity() ? "Self" : "Player";
            }

            String message = Config.getDeathMessage(source, end);
            message = message.replace("{player}", player).replace("{killer}", killer);

            event.setDeathMessage(message);
        }
    }
}
