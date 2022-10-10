package com.strangeone101.holoitemsapi.loot;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

import java.util.List;
import java.util.Random;

public interface LootTableExtension {

    /**
     * Calculate the extra drops this block should drop
     * @param table The loot table we are populating
     * @param drops The list of extra drops. Use list.add(...) to add items to the drops
     * @param random The seeded random
     * @param player The player generating the loot
     * @return Whether the block should drop its normal drops or not
     */
    boolean populateLoot(LootTable table, List<ItemStack> drops, Random random, Player player);
}
