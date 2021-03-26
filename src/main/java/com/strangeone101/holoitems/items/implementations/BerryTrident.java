package com.strangeone101.holoitems.items.implementations;

import com.strangeone101.holoitems.Keys;
import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitems.HoloItemsPlugin;
import com.strangeone101.holoitems.abilities.BerryTridentAbility;
import com.strangeone101.holoitemsapi.interfaces.Interactable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class BerryTrident extends CustomItem implements Interactable {
    public BerryTrident() {
        super("berry_trident", Material.SWEET_BERRIES);
        this.setDisplayName(ChatColor.RED + "Berry Trident");
        this.addLore(ChatColor.GRAY + "A throwable berry trident!")
                .addLore("")
                .addLore(ChatColor.GOLD + "Item Ability: Sweet Return " + ChatColor.YELLOW + "RIGHT CLICK")
                .addLore(ChatColor.GRAY + "Can be thrown with right click like a trident!")
                .addLore("")
                .addLore(ChatColor.GRAY + "Durability: {durability}");
    }

    @Override
    public ItemStack buildStack(Player player) {
        ItemStack stack = super.buildStack(player);
        ItemMeta meta = stack.getItemMeta();

        //Add our custom data to built stacks
        meta.getPersistentDataContainer().set(Keys.getKeys().BERRY_TRIDENT_THROWN, PersistentDataType.INTEGER, 0);

        stack.setItemMeta(meta);

        return stack; //Fix the lore for our added things
    }

    @Override
    public int getMaxDurability() {
        return 186;
    }

    @Override
    public boolean isStackable() {
        return false;
    }

    @Override
    public boolean onInteract(Player player, CustomItem item, ItemStack stack) {
        new BerryTridentAbility(player, stack, player.getInventory(), 0);
        return true;
    }
}
