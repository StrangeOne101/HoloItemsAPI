package com.strangeone101.holoitemsapi;

import org.bukkit.NamespacedKey;

public class Keys {

    //Generic Keys

    /** The key for storing custom item IDs. Should be <strong>STRING</strong> */
    public final NamespacedKey CUSTOM_ITEM_ID = new NamespacedKey(HoloItemsAPI.getPlugin(), "customitemid");
    /** The key for storing custom item owner UUIDs. Should be <strong>UUID</strong> */
    public final NamespacedKey CUSTOM_ITEM_OWNER = new NamespacedKey(HoloItemsAPI.getPlugin(), "itemowner");
    /** The key for storing custom item owner UUIDs. Should be <strong>STRING</strong> */
    public final NamespacedKey CUSTOM_ITEM_OWNER_NAME = new NamespacedKey(HoloItemsAPI.getPlugin(), "itemownername");
    /** The key for storing when the item can be used next. Should be <strong>LONG</strong> */
    public final NamespacedKey CUSTOM_ITEM_COOLDOWN = new NamespacedKey(HoloItemsAPI.getPlugin(), "itemcooldown");
    /** The key for storing custom durability. Should be <strong>INT</strong> */
    public final NamespacedKey CUSTOM_ITEM_DURABILITY = new NamespacedKey(HoloItemsAPI.getPlugin(), "itemdurability");
    /** The key for making it unstackable. Should be <strong>INT</strong> */
    public final NamespacedKey CUSTOM_ITEM_UNSTACK = new NamespacedKey(HoloItemsAPI.getPlugin(), "unstackable");
    /** The key for making it renamable. Should be <strong>INT</strong> */
    public final NamespacedKey CUSTOM_ITEM_RENAME = new NamespacedKey(HoloItemsAPI.getPlugin(), "rename");

}
