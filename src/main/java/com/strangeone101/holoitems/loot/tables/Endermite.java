package com.strangeone101.holoitems.loot.tables;

import com.strangeone101.holoitems.HoloItemsPlugin;
import com.strangeone101.holoitems.items.Items;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Endermite implements LootTable {

    private NamespacedKey key = new NamespacedKey(HoloItemsPlugin.INSTANCE, "endermite_etherreal_essense");

    @Override
    public Collection<ItemStack> populateLoot(Random random, LootContext context) {
        List<ItemStack> drops = new ArrayList<>();

        int bonus = context.getLootingModifier() > 2 ? random.nextInt(context.getLootingModifier()) : 1;
        int amount = random.nextInt(2);
        if (bonus > 1) amount += random.nextInt(bonus);

        if (amount > 0) {
            ItemStack stack = Items.ETHERREAL_ESSENSE.buildStack(null);
            stack.setAmount(amount);
            drops.add(stack);
        }

        return drops;
    }

    @Override
    public void fillInventory(Inventory inventory, Random random, LootContext context) {

    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }
}
