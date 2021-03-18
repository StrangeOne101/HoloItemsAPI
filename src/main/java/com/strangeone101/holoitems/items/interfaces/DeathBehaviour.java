package com.strangeone101.holoitems.items.interfaces;

import com.strangeone101.holoitems.CustomItem;
import com.strangeone101.holoitems.util.ListenerContext;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public interface DeathBehaviour extends BehaviourListener<EntityDeathEvent> {

    @Override
    public default ListenerContext<EntityDeathEvent> buildContext(CustomItem item, ItemStack stack, Player player, ListenerContext.Position position, EntityDeathEvent event) {
        return new ListenerContext<>(player, item, stack, event, position);
    }

    @Override
    public default Class<? extends Event> getEventClass() {
        return EntityDeathEvent.class;
    }
}

