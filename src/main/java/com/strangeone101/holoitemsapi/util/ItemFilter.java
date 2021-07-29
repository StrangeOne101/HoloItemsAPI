package com.strangeone101.holoitemsapi.util;

import org.bukkit.inventory.ItemStack;

public interface ItemFilter {

    boolean accepts(ItemStack stack);
}
