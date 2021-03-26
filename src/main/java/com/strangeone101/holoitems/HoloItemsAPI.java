package com.strangeone101.holoitems;

import com.strangeone101.holoitems.listener.AbilityListener;
import com.strangeone101.holoitems.listener.GenericListener;
import com.strangeone101.holoitems.listener.ItemListener;
import com.strangeone101.holoitems.listener.LootListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class HoloItemsAPI {

    private static JavaPlugin plugin;

    public static boolean setup(JavaPlugin plugin) {
        if (HoloItemsAPI.plugin != null) {
            plugin.getLogger().warning("Failed to setup HoloItemsAPI! Already setup with " + HoloItemsAPI.plugin.getName());
            return false;
        }

        HoloItemsAPI.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(new ItemListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new AbilityListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new LootListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new GenericListener(), plugin);

        //Make all item abilities tick
        new ItemAbility.CustomItemAbilityTask().runTaskTimer(plugin, 1L, 1L);

        return true;
    }

    public static void shutdown() {
        ItemAbility.stopPluginAndRemove();
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }
}
