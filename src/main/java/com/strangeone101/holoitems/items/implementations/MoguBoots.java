package com.strangeone101.holoitems.items.implementations;

import com.strangeone101.holoitemsapi.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.concurrent.ThreadLocalRandom;

public class MoguBoots extends CustomItem {

    public MoguBoots() {
        super("mogu_boots", Material.LEATHER_BOOTS);
        this.setDisplayName(ChatColor.LIGHT_PURPLE + "Mogu Boots");
        this.addLore(ChatColor.GRAY + "Makes mogu sounds when you walk");
    }

    @Override
    public ItemStack buildStack(Player player) {
        ItemStack stack = super.buildStack(player);

        ItemMeta meta = stack.getItemMeta();

        if (meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(Color.fromBGR(0x7706c2));
        }
        stack.setItemMeta(meta);

        return stack;
    }

    public static void mogu(Location location) {
        int i = ThreadLocalRandom.current().nextInt(5);
        if (i < 2) {
            float pitchMod = (float) (ThreadLocalRandom.current().nextFloat() * 0.5 - 0.5);
            location.getWorld().playSound(location, i == 0 ? Sound.ENTITY_CAT_PURR : Sound.ENTITY_CAT_PURREOW, 1F, 1F + pitchMod);
        }
    }
}
