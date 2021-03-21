package com.strangeone101.holoitems.items.interfaces;


import com.strangeone101.holoitems.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * For items that should be right clicked to activate
 */
public interface Interactable {

    /**
     * Is fired when the Custom Item is right clicked
     *
     * @param player The player that right clicked
     * @param item The custom item
     * @param stack The item stack
     * @return Whether to cancel the interact event. Should be true.
     */
    boolean onInteract(Player player, CustomItem item, ItemStack stack);
}
