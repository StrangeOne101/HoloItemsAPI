package com.strangeone101.holoitemsapi.loot;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

/**
 * Defines a custom loot table for blocks
 */
public interface BlockLootTable {

    /**
     * Calculate the extra drops this block should drop
     * @param extraDrops The list of extra drops. Use list.add(...) to add items to the drops
     * @param random The seeded random
     * @param context The context of the block drop
     * @return Whether the block should drop its normal drops or not
     */
    boolean populateLoot(List<ItemStack> extraDrops, Random random, BlockLootContext context);

    //So if I added an int for the exp in the parameters, it wouldn't update if I go `exp *= 3;` as that
    //would change the one within the populateLoot method. Not the original. Since its primitive
}
