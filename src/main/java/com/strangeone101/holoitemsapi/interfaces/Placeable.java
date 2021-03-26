package com.strangeone101.holoitemsapi.interfaces;

import com.strangeone101.holoitemsapi.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Placeable {

    /**
     * Fired when the CustomItem is placed. Return true to cancel the placement
     * @param block The block being placed
     * @param player The player placing it
     * @param item The item being placed
     * @param stack The item stack
     * @return If the event should cancel the place event
     */
    boolean place(Block block, Player player, CustomItem item, ItemStack stack);
}
