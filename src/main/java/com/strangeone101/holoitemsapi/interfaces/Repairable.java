package com.strangeone101.holoitemsapi.interfaces;

import com.strangeone101.holoitemsapi.CustomItem;
import org.bukkit.inventory.ItemStack;

/**
 * An interface for custom items that can be repaired
 */
public interface Repairable {

    /**
     * Get the custom item that can be used to repair
     * this custom item
     * @param stack The itemstack being repaired
     * @return The custom item used to repair
     */
    CustomItem getRepairMaterial(ItemStack stack);

    /**
     * Get the amount it should repair. Should be a value
     * between 0.0 to 1.0. 1.0 is a 100%
     * @param stack The itemstack being repaired
     * @return The amount to repair as a decimal (0.0 to 1.0)
     */
    double getRepairAmount(ItemStack stack);
}
