package com.strangeone101.holoitems;

import com.strangeone101.holoitems.util.UUIDTagType;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    }

    public static CustomItem getCustomItem(String id) {
        return CUSTOM_ITEMS.get(id);
    }

    public static boolean isCustomItem(ItemStack stack) {
        return stack != null && stack.hasItemMeta() && stack.getItemMeta().getPersistentDataContainer().has(HoloItemsPlugin.getKeys().CUSTOM_ITEM_ID, PersistentDataType.STRING);
    }

    public static CustomItem getCustomItem(ItemStack stack) {
        if (isCustomItem(stack)) {
            String id = stack.getItemMeta().getPersistentDataContainer().get(HoloItemsPlugin.getKeys().CUSTOM_ITEM_ID, PersistentDataType.STRING);
            CustomItem item = getCustomItem(id);

            return item;
        }

        return null;
    }

    /**
     * When an ItemStack with an invalid ID is found, it should transform into this
     * @param stack
     */
    public static void invalidateItemstack(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        String id = stack.getItemMeta().getPersistentDataContainer().get(HoloItemsPlugin.getKeys().CUSTOM_ITEM_ID, PersistentDataType.STRING);
        String owner = "?";
        String ownerName = "?";

        if (stack.getItemMeta().getPersistentDataContainer().has(HoloItemsPlugin.getKeys().CUSTOM_ITEM_OWNER, UUIDTagType.TYPE)) {
            owner = stack.getItemMeta().getPersistentDataContainer().get(HoloItemsPlugin.getKeys().CUSTOM_ITEM_OWNER, UUIDTagType.TYPE).toString();
        }
        if (stack.getItemMeta().getPersistentDataContainer().has(HoloItemsPlugin.getKeys().CUSTOM_ITEM_OWNER_NAME, PersistentDataType.STRING)) {
            ownerName = stack.getItemMeta().getPersistentDataContainer().get(HoloItemsPlugin.getKeys().CUSTOM_ITEM_OWNER_NAME, PersistentDataType.STRING);
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

    public static Map<String, CustomItem> getCustomItems() {
        return CUSTOM_ITEMS;
    }

}
