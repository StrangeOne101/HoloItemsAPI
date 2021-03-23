package com.strangeone101.holoitems.items.interfaces;

import com.strangeone101.holoitems.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Edible {

    int getHungerAmount();

    float getSaturationAmount();

    default void onEat(Player player, CustomItem item, ItemStack stack) { }

    static long NORMAL_LENGTH = 1600;
    static long SHORT_LENGTH = 800;
    static long LONG_LENGTH = 3200;

    default long getEatDuration() {
        return NORMAL_LENGTH;
    }

    default boolean eatWhenFull() {
        return false;
    }

}
