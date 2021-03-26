package com.strangeone101.holoitems.items.implementations;

import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitemsapi.Properties;
import com.strangeone101.holoitemsapi.interfaces.Placeable;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantedBlock extends CustomItem implements Placeable {

    public EnchantedBlock(String name, Material material, String localizedType) {
        super(name, material);
        this.addLore("");
        this.addLore(ChatColor.GOLD + "Item Ability: Infinity")
                .addLore(ChatColor.GRAY + "This block of " + ChatColor.YELLOW + localizedType + ChatColor.GRAY + " will never ever")
                .addLore(ChatColor.GRAY + "run out!");
        this.addProperty(Properties.UNSTACKABLE);
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

    @Override
    public boolean place(Block block, Player player, CustomItem item, ItemStack stack) {
        if (player.getGameMode() != GameMode.CREATIVE) {
            stack.setAmount(stack.getAmount() + 1);
        }

        return false;
    }
}
