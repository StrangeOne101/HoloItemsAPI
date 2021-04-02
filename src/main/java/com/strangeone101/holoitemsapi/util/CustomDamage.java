package com.strangeone101.holoitemsapi.util;

import com.strangeone101.holoitemsapi.event.CIDamageEntityEvent;
import com.strangeone101.holoitemsapi.event.CIEntityDamageEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class CustomDamage {

    /**
     * Damage an entity with a custom damage source
     * @param entity The entity to damage
     * @param source The entity that caused the damage. Can be null.
     * @param damage The amount of damage
     * @param damageSource The custom damage source
     * @param ignoreArmor Whether the damage should ignore armor
     */
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
            if (ignoreArmor || damageSource.doesIgnoreArmor()) {
                if (damageEvent.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)) {
                    damageEvent.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
                }
            }
        }
    }

    /**
     * Damage an entity with a custom damage source
     * @param entity The entity to damage
     * @param source The entity that caused the damage. Can be null.
     * @param damage The amount of damage
     * @param damageSource The custom damage source
     */
    public static void damageEntity(final Entity entity, final Entity source, final double damage, final CustomDamageSource damageSource) {
        damageEntity(entity, source, damage, damageSource, false);
    }

    /**
     * Damage an entity with a custom damage source
     * @param entity The entity to damage
     * @param damage The amount of damage
     * @param damageSource The custom damage source
     */
    public static void damageEntity(final Entity entity, final double damage, final CustomDamageSource damageSource) {
        damageEntity(entity, null, damage, damageSource);
    }
}
