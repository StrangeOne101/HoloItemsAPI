package com.strangeone101.holoitems.items.implementations;

import com.strangeone101.holoitems.CustomItem;
import com.strangeone101.holoitems.items.interfaces.Edible;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ScrambledEgg extends CustomItem implements Edible {

    public ScrambledEgg(String string, Material material) {
        super(string, material);
        this.addLore(ChatColor.DARK_GRAY + "Food Item");
    }

    @Override
    public int getHungerAmount() {
        return 3;
    }

    @Override
    public float getSaturationAmount() {
        return 2;
    }
}
