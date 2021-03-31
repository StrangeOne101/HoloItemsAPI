package com.strangeone101.holoitemsapi;

import com.strangeone101.holoitemsapi.itemevent.EventCache;
import com.strangeone101.holoitemsapi.listener.GenericListener;
import com.strangeone101.holoitemsapi.listener.ItemListener;
import com.strangeone101.holoitemsapi.listener.LootListener;
import com.strangeone101.holoitemsapi.recipe.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class HoloItemsAPI {

    private static JavaPlugin plugin;
    private static Keys keys;

    public static boolean setup(JavaPlugin plugin) {
        if (HoloItemsAPI.plugin != null) {
            plugin.getLogger().warning("Failed to setup HoloItemsAPI! Already setup with " + HoloItemsAPI.plugin.getName());
            return false;
        }

        HoloItemsAPI.plugin = plugin;

        keys = new Keys();

        Bukkit.getPluginManager().registerEvents(new ItemListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new LootListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new GenericListener(), plugin);

        //Make all item abilities tick
        new ItemAbility.CustomItemAbilityTask().runTaskTimer(plugin, 1L, 1L);

        //Prepares caches after caller's onEnable() finishes
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            //Create cache for all players
            for (Player player : Bukkit.getOnlinePlayers()) {
                EventCache.fullCache(player);
            }
        }, 1L);

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

    public static void setGeneralConfig(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Config.generalConfig = config;
        Config.generalConfigFile = file;
    }

    public static void setDeathMessageConfig(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Config.deathMessageConfig = config;
        Config.deathMessageConfigFile = file;
    }
}
