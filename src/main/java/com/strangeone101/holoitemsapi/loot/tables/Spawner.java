package com.strangeone101.holoitemsapi.loot.tables;

import com.strangeone101.holoitemsapi.loot.BlockLootContext;
import com.strangeone101.holoitemsapi.loot.BlockLootTable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class Spawner implements BlockLootTable {

    @Override
    public boolean populateLoot(List<ItemStack> extraDrops, Random random, BlockLootContext context) {
        int bottleAmounts = random.nextInt(4 + context.getFortune()) + 2 + (context.getFortune() / 2);
        extraDrops.add(new ItemStack(Material.EXPERIENCE_BOTTLE, bottleAmounts));
        return false;
    }
}
