package com.strangeone101.holoitemsapi.recipe;

import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitemsapi.CustomItemRegistry;
import com.strangeone101.holoitemsapi.util.ItemFilter;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class RecipeItemFilter implements ItemFilter {

    private ItemStack[] items = new ItemStack[0];

    RecipeItemFilter(ItemStack... items) {
        this.items = items;
    }

    public boolean accepts(ItemStack stack) {
        if (CustomItemRegistry.isCustomItem(stack)) {
            CustomItem stackci = CustomItemRegistry.getCustomItem(stack);

            for (ItemStack item : items) {
                if (CustomItemRegistry.getCustomItem(item) == stackci &&
                        (stackci.getMaxDurability() == 0 || stackci.getDurability(stack) == stackci.getDurability(item)))
                    return true;
            }
            return false;
        } else {
            for (ItemStack item : items) {
                if (stack.isSimilar(item)) return true;
            }
            return false;
        }
    }
}
