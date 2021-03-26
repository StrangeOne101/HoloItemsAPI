package com.strangeone101.holoitemsapi;

import com.strangeone101.holoitemsapi.listener.GenericListener;
import com.strangeone101.holoitemsapi.listener.ItemListener;
import com.strangeone101.holoitemsapi.listener.LootListener;
import com.strangeone101.holoitemsapi.recipe.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class HoloItemsAPI {

    private static JavaPlugin plugin;
    private static Keys keys;

    public static boolean setup(JavaPlugin plugin) {
        if (HoloItemsAPI.plugin != null) {
            plugin.getLogger().warning("Failed to setup HoloItemsAPI! Already setup with " + HoloItemsAPI.plugin.getName());
            return false;
        }

        HoloItemsAPI.plugin = plugin;

        keys = new Keys(plugin);

        Bukkit.getPluginManager().registerEvents(new ItemListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new LootListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new GenericListener(), plugin);

        //Make all item abilities tick
        new ItemAbility.CustomItemAbilityTask().runTaskTimer(plugin, 1L, 1L);

        //Create cache for all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            EventContext.fullCache(player);
        }

        return true;
    }

    public static void shutdown() {
        ItemAbility.stopPluginAndRemove();
        RecipeManager.unregisterAll();
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static Keys getKeys() {
        return keys;
    }
}
