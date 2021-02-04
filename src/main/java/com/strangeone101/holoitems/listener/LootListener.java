package com.strangeone101.holoitems.listener;

import com.strangeone101.holoitems.HoloItemsPlugin;
import com.strangeone101.holoitems.loot.CustomLootRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class LootListener implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        HoloItemsPlugin.INSTANCE.getLogger().info("Debug here 333");
        CustomLootRegistry.handleDeath(event.getEntity(), event.getDrops());
    }
}
