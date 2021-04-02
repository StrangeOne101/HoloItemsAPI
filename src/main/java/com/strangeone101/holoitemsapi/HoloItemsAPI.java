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

    /**
     * Setup the HoloItemsAPI for the provided plugin to be used as a base. This should be called within `onEnable()`
     * before any items are created.
     * @param plugin The plugin
     * @return True if setup was successful
     */
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

    /**
     * Stops all functions started by the HoloItemsAPI. Should be called within `onDisable()`
     */
    public static void shutdown() {
        ItemAbility.stopPluginAndRemove();
        RecipeManager.unregisterAll();
    }

    /**
     * Get the plugin associated with the HoloItemsAPI
     * @return The plugin
     */
    public static JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * Get the default NamespaceKeys that are used within the API
     * @return The keys instance
     */
    public static Keys getKeys() {
        return keys;
    }

    /**
     * Set a file to be used as a general config file. Should be a
     * yml file. Is optional.
     * @param file The file to use as a general config
     */
    public static void setGeneralConfig(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Config.generalConfig = config;
        Config.generalConfigFile = file;
    }

    /**
     * Set a file to be used as a death message config file. Should be a
     * yml file. Is optional.
     * @param file The file to use as a death message config
     */
    public static void setDeathMessageConfig(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        Config.deathMessageConfig = config;
        Config.deathMessageConfigFile = file;
    }
}
