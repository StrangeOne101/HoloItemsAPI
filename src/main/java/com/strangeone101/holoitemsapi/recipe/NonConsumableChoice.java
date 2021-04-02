package com.strangeone101.holoitemsapi.recipe;

import org.bukkit.inventory.ItemStack;

/**
 * A recipe choice item that will not be consumed in recipes. Like how
 * milk buckets do not consume the bucket when used in crafting
 */
public class NonConsumableChoice extends CIRecipeChoice {

    public NonConsumableChoice(ItemStack... itemStacks) {
        super(itemStacks);
    }

}
