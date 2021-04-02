package com.strangeone101.holoitemsapi;

import com.strangeone101.holoitemsapi.itemevent.EventCache;
import com.strangeone101.holoitemsapi.util.UUIDTagType;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A registry for managing all custom items
 */
public class CustomItemRegistry {

    private static int NEXT_ID = 2300;
    private static final int INVALID_ID = 404;

    private static Map<String, CustomItem> CUSTOM_ITEMS = new HashMap<>();

    /**
     * Register a custom item
     * @param item The item
     */
    public static void register(CustomItem item) {
        CUSTOM_ITEMS.put(item.getInternalName(), item);

        if (item.getInternalID() == 0) {
            item.setInternalID(NEXT_ID);

            NEXT_ID++;
            if (NEXT_ID == INVALID_ID) NEXT_ID++;
        }

        EventCache.registerEvents(item);
    }

    /**
     * Get a custom item from the item identifier
     * @param id The item identifier
     * @return The custom item
     */
    public static CustomItem getCustomItem(String id) {
        return CUSTOM_ITEMS.get(id);
    }

    /**
     * Is this a custom item?
     * @param stack The itemstack
     * @return True if it's a custom item
     */
    public static boolean isCustomItem(ItemStack stack) {
        return stack != null && stack.hasItemMeta() && stack.getItemMeta().getPersistentDataContainer().has(HoloItemsAPI.getKeys().CUSTOM_ITEM_ID, PersistentDataType.STRING);
    }

    /**
     * Get the CustomItem from the provided stack
     * @param stack The itemstack
     * @return The custom item
     */
    public static CustomItem getCustomItem(ItemStack stack) {
        if (isCustomItem(stack)) {
            String id = stack.getItemMeta().getPersistentDataContainer().get(HoloItemsAPI.getKeys().CUSTOM_ITEM_ID, PersistentDataType.STRING);

            return getCustomItem(id);
        }

        return null;
    }

    /**
     * When an ItemStack with an invalid ID is found, it should transform into this
     * @param stack The itemstack
     */
    public static void invalidateItemstack(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        String id = stack.getItemMeta().getPersistentDataContainer().get(HoloItemsAPI.getKeys().CUSTOM_ITEM_ID, PersistentDataType.STRING);
        String owner = "?";
        String ownerName = "?";

        if (stack.getItemMeta().getPersistentDataContainer().has(HoloItemsAPI.getKeys().CUSTOM_ITEM_OWNER, UUIDTagType.TYPE)) {
            owner = stack.getItemMeta().getPersistentDataContainer().get(HoloItemsAPI.getKeys().CUSTOM_ITEM_OWNER, UUIDTagType.TYPE).toString();
        }
        if (stack.getItemMeta().getPersistentDataContainer().has(HoloItemsAPI.getKeys().CUSTOM_ITEM_OWNER_NAME, PersistentDataType.STRING)) {
            ownerName = stack.getItemMeta().getPersistentDataContainer().get(HoloItemsAPI.getKeys().CUSTOM_ITEM_OWNER_NAME, PersistentDataType.STRING);
        }
        meta.setDisplayName(ChatColor.RED + "Invalid Item");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Can you not break things for one moment???");
        lore.add("");
        lore.add(ChatColor.DARK_GRAY + "Internal ID: " + ChatColor.GRAY + id);
        lore.add(ChatColor.DARK_GRAY + "Owner: " + ChatColor.GRAY + owner);
        lore.add(ChatColor.DARK_GRAY + "Owner Name: " + ChatColor.GRAY + ownerName);

        meta.setLore(lore);
        meta.setCustomModelData(INVALID_ID); //Set custom model for invalid item
        stack.setItemMeta(meta);
    }

    /**
     * Get all custom items
     * @return The many many items
     */
    public static Map<String, CustomItem> getCustomItems() {
        return CUSTOM_ITEMS;
    }

}
