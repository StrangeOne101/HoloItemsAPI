package com.strangeone101.holoitems.items.interfaces;

import com.strangeone101.holoitems.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Edible {

    public int getHungerAmount();

    public float getSaturationAmount();

    public default void onEat(Player player, CustomItem item, ItemStack stack) { }

    public static final long NORMAL_LENGTH = 1600;
    public static final long SHORT_LENGTH = 800;
    public static final long LONG_LENGTH = 3200;

    public default long getEatDuration() {
        return NORMAL_LENGTH;
    }


}
