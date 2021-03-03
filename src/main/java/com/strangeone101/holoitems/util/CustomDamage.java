package com.strangeone101.holoitems.util;

import com.strangeone101.holoitems.event.CIDamageEntityEvent;
import com.strangeone101.holoitems.event.CIEntityDamageEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class CustomDamage {

    public static void damageEntity(final Entity entity, Entity source, double damage, final CustomDamageSource damageSource, boolean ignoreArmor) {
        if (damageSource == null) {
            return;
        }

        if (!(entity instanceof LivingEntity)) {
            return;
        }

        CIDamageEntityEvent damageEvent = new CIDamageEntityEvent(entity, damageSource, damage);

        if (source != null) {
            damageEvent = new CIEntityDamageEntityEvent(entity, source, damageSource, damage);
        }
        Bukkit.getServer().getPluginManager().callEvent(damageEvent);

        if (!damageEvent.isCancelled()) {
            damage = damageEvent.getDamage();

            final double prevHealth = ((LivingEntity) entity).getHealth();
            if (source == null) {
                ((LivingEntity) entity).damage(damage);
            } else {
                ((LivingEntity) entity).damage(damage, source);
            }
            final double nextHealth = ((LivingEntity) entity).getHealth();
            entity.setLastDamageCause(damageEvent);
            if (ignoreArmor) {
                if (damageEvent.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)) {
                    damageEvent.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
                }
            }
        }
    }

    public static void damageEntity(final Entity entity, final Entity source, final double damage, final CustomDamageSource damageSource) {
        damageEntity(entity, source, damage, damageSource, false);
    }

    public static void damageEntity(final Entity entity, final double damage, final CustomDamageSource damageSource) {
        damageEntity(entity, null, damage, damageSource);
    }
}
