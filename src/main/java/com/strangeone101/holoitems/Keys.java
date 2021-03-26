package com.strangeone101.holoitems;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class Keys {

    private JavaPlugin plugin;

    public Keys(JavaPlugin plugin) {
        this.plugin = plugin;
        keys = this;
    }

    //Rushia Shield

    /** The key for how many mobs are stored. Should be <strong>INT</strong> */
    public final NamespacedKey RUSHIA_SHIELD_COUNT = new NamespacedKey(plugin, "rushia_shield_count");
    /** The key for the types of mobs. Should be <strong>STRING</strong> */
    public final NamespacedKey RUSHIA_SHIELD_MOBS = new NamespacedKey(plugin, "rushia_shield_mobs");

    /** The key for the types of mobs. Should be <strong>INT</strong> */
    public final NamespacedKey BERRY_TRIDENT_THROWN = new NamespacedKey(plugin, "berry_trident");

    /** How many clicks until death. Should be <strong>INT</strong> */
    public final NamespacedKey RUSSIAN_ROULETTE = new NamespacedKey(plugin, "russian_roulette");

    private static Keys keys;

    public static Keys getKeys() {
        return keys;
    }
}
