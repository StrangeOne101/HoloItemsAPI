package com.strangeone101.holoitemsapi.listener;

import com.strangeone101.holoitemsapi.loot.CustomLootRegistry;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.LootGenerateEvent;

public class LootListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST) //Let all plugins go before us
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().getWorld().getGameRuleValue(GameRule.DO_MOB_LOOT)) {
            CustomLootRegistry.handleDeath(event.getEntity(), event.getDrops());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST) //Let all plugins go before us
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer() != null && event.getBlock().getWorld().getGameRuleValue(GameRule.DO_TILE_DROPS)
                && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            CustomLootRegistry.handleBlockBreak(event);
        }
    }

    @EventHandler
    public void onLootCreate(LootGenerateEvent event) {

    }
}
