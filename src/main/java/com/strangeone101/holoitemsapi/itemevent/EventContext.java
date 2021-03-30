package com.strangeone101.holoitemsapi.itemevent;

import com.strangeone101.holoitemsapi.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EventContext {

    private Player player;
    private CustomItem item;
    private ItemStack stack;
    private Position position;

    public EventContext(Player player, CustomItem item, ItemStack stack, Position position) {
        this.player = player;
        this.item = item;
        this.stack = stack;
        this.position = position;
    }

    public CustomItem getItem() {
        return item;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Position getPosition() {
        return position;
    }

    public Player getPlayer() {
        return player;
    }

}
