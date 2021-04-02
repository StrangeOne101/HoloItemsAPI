package com.strangeone101.holoitemsapi.interfaces;

import com.strangeone101.holoitemsapi.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * An interface to run code when a player interacts with a block using the item
 */
public interface BlockInteractable {

    /**
     * Is fired when a block is clicked by a Custom Item
     *
     * @param player The player that clicked
     * @param block The block clicked
     * @param item The custom item
     * @param stack The item stack
     * @param leftClick If the click was a left click. False for right clicks.
     * @return Whether the interact event should be cancelled. Should be true
     */
    boolean onInteract(Player player, Block block, CustomItem item, ItemStack stack, boolean leftClick);
}
