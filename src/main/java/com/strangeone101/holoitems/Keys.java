package com.strangeone101.holoitems;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class Keys {

    private JavaPlugin plugin;

    public Keys(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    //Generic Keys

    /** The key for storing custom item IDs. Should be <strong>STRING</strong> */
    public final NamespacedKey CUSTOM_ITEM_ID = new NamespacedKey(plugin, "customitemid");
    /** The key for storing custom item owner UUIDs. Should be <strong>UUID</strong> */
    public final NamespacedKey CUSTOM_ITEM_OWNER = new NamespacedKey(plugin, "itemowner");
    /** The key for storing custom item owner UUIDs. Should be <strong>STRING</strong> */
    public final NamespacedKey CUSTOM_ITEM_OWNER_NAME = new NamespacedKey(plugin, "itemownername");
    /** The key for storing when the item can be used next. Should be <strong>LONG</strong> */
    public final NamespacedKey CUSTOM_ITEM_COOLDOWN = new NamespacedKey(plugin, "itemcooldown");
    /** The key for storing custom durability. Should be <strong>INT</strong> */
    public final NamespacedKey CUSTOM_ITEM_DURABILITY = new NamespacedKey(plugin, "itemdurability");
    /** The key for making it unstackable. Should be <strong>INT</strong> */
    public final NamespacedKey CUSTOM_ITEM_UNSTACK = new NamespacedKey(plugin, "unstackable");
    /** The key for making it renamable. Should be <strong>INT</strong> */
    public final NamespacedKey CUSTOM_ITEM_RENAME = new NamespacedKey(plugin, "rename");


    //Rushia Shield

    /** The key for how many mobs are stored. Should be <strong>INT</strong> */
    public final NamespacedKey RUSHIA_SHIELD_COUNT = new NamespacedKey(plugin, "rushia_shield_count");
    /** The key for the types of mobs. Should be <strong>STRING</strong> */
    public final NamespacedKey RUSHIA_SHIELD_MOBS = new NamespacedKey(plugin, "rushia_shield_mobs");

    /** The key for the types of mobs. Should be <strong>INT</strong> */
    public final NamespacedKey BERRY_TRIDENT_THROWN = new NamespacedKey(plugin, "berry_trident");

    /** How many clicks until death. Should be <strong>INT</strong> */
    public final NamespacedKey RUSSIAN_ROULETTE = new NamespacedKey(plugin, "russian_roulette");

}
