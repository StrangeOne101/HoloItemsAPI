package com.strangeone101.holoitems.items.interfaces;

import com.strangeone101.holoitems.CustomItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface EntityInteractable {

    public boolean onInteract(Entity entity, Player player, CustomItem item, ItemStack stack);
}
