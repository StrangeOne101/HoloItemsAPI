package com.strangeone101.holoitemsapi.event;

import com.strangeone101.holoitemsapi.util.CustomDamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class CIDamageEntityEvent extends EntityDamageEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled;
    private CustomDamageSource damageSource;
    private double damage;

    public CIDamageEntityEvent(Entity what, CustomDamageSource damageSource, double damage) {
        super(what, DamageCause.CUSTOM, damage);

        this.damageSource = damageSource;
        this.damage = damage;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public CustomDamageSource getDamageSource() {
        return damageSource;
    }

    public double getDamage() {
        return damage;
    }
}
