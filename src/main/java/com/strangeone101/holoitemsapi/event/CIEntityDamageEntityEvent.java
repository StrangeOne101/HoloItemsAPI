package com.strangeone101.holoitemsapi.event;

import com.strangeone101.holoitemsapi.util.CustomDamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;

public class CIEntityDamageEntityEvent extends CIDamageEntityEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private Entity source;

    public CIEntityDamageEntityEvent(Entity what, Entity source, CustomDamageSource damageSource, double damage) {
        super(what, damageSource, damage);

        this.source = source;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Entity getDamagee() {
        return source;
    }
}
