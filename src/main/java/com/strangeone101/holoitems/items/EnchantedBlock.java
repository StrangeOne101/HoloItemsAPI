package com.strangeone101.holoitems.items;

import com.strangeone101.holoitems.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantedBlock extends CustomItem {

    public EnchantedBlock(String name, Material material, String localizedType) {
        super(name, material);
        this.addLore("");
        this.addLore(ChatColor.GOLD + "Item Ability: Infinity")
                .addLore(ChatColor.GRAY + "This block of " + ChatColor.YELLOW + localizedType + ChatColor.GRAY + " will never ever")
                .addLore(ChatColor.GRAY + "run out!");
    }

    @Override
    public ItemStack buildStack(Player player) {
        ItemStack stack = super.buildStack(player);
        stack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        ItemMeta meta = stack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        return stack;
    }
}
