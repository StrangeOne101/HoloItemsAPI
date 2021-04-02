package com.strangeone101.holoitemsapi.itemevent;

import com.strangeone101.holoitemsapi.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The context of an {@link ItemEvent} firing
 */
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

    /**
     * The item in the event
     * @return The custom item
     */
    public CustomItem getItem() {
        return item;
    }

    /**
     * The custom item stack in the event
     * @return The itemstack
     */
    public ItemStack getStack() {
        return stack;
    }

    /**
     * The position of the item within the player's inventory
     * @return The position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * The player involved
     * @return The player
     */
    public Player getPlayer() {
        return player;
    }

}
