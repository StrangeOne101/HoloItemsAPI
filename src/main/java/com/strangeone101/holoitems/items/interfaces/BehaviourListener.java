package com.strangeone101.holoitems.items.interfaces;

import com.strangeone101.holoitems.CustomItem;
import com.strangeone101.holoitems.util.ListenerContext;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public interface BehaviourListener<E extends Event> {

    public void onTrigger(ListenerContext<E> context);

    public ListenerContext<E> buildContext(CustomItem item, ItemStack stack, Player player, ListenerContext.Position position, E event);

    public Class<? extends Event> getEventClass();
}
