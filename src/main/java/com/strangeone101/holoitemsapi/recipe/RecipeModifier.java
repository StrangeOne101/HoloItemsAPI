package com.strangeone101.holoitemsapi.recipe;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface RecipeModifier {

    ItemStack create(ItemStack baseOutput, Map<RecipeGroup, ItemStack> filters, RecipeContext context);
}
