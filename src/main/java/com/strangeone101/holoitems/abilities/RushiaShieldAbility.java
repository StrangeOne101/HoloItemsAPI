package com.strangeone101.holoitems.abilities;

import com.strangeone101.holoitems.CustomItem;
import com.strangeone101.holoitems.ItemAbility;
import com.strangeone101.holoitems.CustomItemRegistry;
import com.strangeone101.holoitems.HoloItemsPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class RushiaShieldAbility extends ItemAbility {

    public static final boolean GLOW_MODE = true;
    public static final HashSet<Mob> SHIELD_MOBS = new HashSet<>();

    private boolean mobSpawned = false;
    private Mob mob = null;

    public RushiaShieldAbility(Player player, ItemStack stack, Inventory inventory, int slot) {
        super(player, stack, inventory, slot);

        if (ItemAbility.isAbilityActive(player, this.getClass()) || isOnCooldown()) return; //Ability already active or on cooldown

        if (stack.getItemMeta().getPersistentDataContainer().get(HoloItemsPlugin.getKeys().RUSHIA_SHIELD_COUNT, PersistentDataType.INTEGER) > 0) {
            start();
        }
    }

    @Override
    public void tick() {
        if (!getPlayer().isHandRaised()) { //isHandRaised is for when the shield is being raised. isBlocking only returns true
            remove();                      //when the shield is FULLY up.
        }

        if (!getPlayer().isBlocking() && !mobSpawned) { //If the shield isn't fully up yet
            //TODO Spawn smoke particles
            Location target = getPlayer().getEyeLocation().add(getPlayer().getEyeLocation().getDirection().clone().multiply(1.5));
            getPlayer().getWorld().spawnParticle(Particle.SMOKE_LARGE, target, 5, 0.6, 0.6, 0.6, 0);
            return;
        }

        if (!mobSpawned) { //If the shield is fully up but the mob hasn't spawned yet
            Location target = getPlayer().getLocation().add(getPlayer().getLocation().getDirection().clone().multiply(1.5));
            String mobString = getStack().getItemMeta().getPersistentDataContainer().get(HoloItemsPlugin.getKeys().RUSHIA_SHIELD_MOBS, PersistentDataType.STRING);
            String mobToUse = mobString.split(";")[0];

            if (mobToUse.equals("")) { //Something went really wrong!
                remove();
                return;
            }

            mobString = mobString.contains(";") ? mobString.split(";", 2)[1] : "";
            int count = mobString.split(";").length - 1;
            EntityType type = EntityType.valueOf(mobToUse);

            mob = (Creature) getPlayer().getWorld().spawnEntity(target, type);
            SHIELD_MOBS.add(mob);
            mobSpawned = true;
            if (GLOW_MODE) {
                mob.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 99999, 0, true, false));
                mob.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 0, true, false));
            }
            mob.setFallDistance(0);
            mob.getEquipment().clear(); //Make sure they have no equipment


            //Get all mobs within 15 blocks and if they are targeting the player, make them target the new shield entity
            for (Entity entity : getPlayer().getWorld().getNearbyEntities(getPlayer().getLocation(), 15, 15, 15)) {
                if (entity instanceof Creature) {
                    if (((Creature) entity).getTarget() == getPlayer()) {
                        ((Creature) entity).setTarget(mob);
                    }
                }
            }

            ItemMeta meta = getStack().getItemMeta();
            meta.getPersistentDataContainer().set(HoloItemsPlugin.getKeys().RUSHIA_SHIELD_MOBS, PersistentDataType.STRING, mobString);
            meta.getPersistentDataContainer().set(HoloItemsPlugin.getKeys().RUSHIA_SHIELD_COUNT, PersistentDataType.INTEGER, count);
            getStack().setItemMeta(meta);

            getItem().updateStack(getStack(), getPlayer());
            //TODO SPAWN MOB, DONE
            //TODO Update Mob data on item DONE

        } else {
            if (mob == null || mob.isDead() || !getPlayer().isBlocking()) {
                //getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&d&kxxx &5Soul Released &d&kxxx"));
                remove(); //Kill the mob and go on cooldown if
                return;
            }

            if (mob.getWorld() != getPlayer().getWorld()) { //If the player got TP'd away for some reason
                remove();
                return;
            }

            Location target = getPlayer().getEyeLocation().add(getPlayer().getEyeLocation().getDirection().clone().multiply(1.5));
            target.subtract(0, mob.getEyeHeight() / 2, 0);

            mob.setFallDistance(0);

            double distance = mob.getLocation().distance(target);
            double dx, dy, dz;
            dx = target.getX() - mob.getLocation().getX();
            dy = target.getY() - mob.getLocation().getY();
            dz = target.getZ() - mob.getLocation().getZ();
            Vector vector = new Vector(dx, dy, dz);
            if (vector.length() > 1) vector.normalize().multiply(.5); //Max speed it should move

            if (distance < 0.1) {
                vector = new Vector(0, 0, 0);
            }

            mob.setVelocity(vector);

            //TODO Move mob. Test if it's dead. DONE
        }
    }

    @Override
    public void remove() {
        super.remove();

        if (mobSpawned) {
            addCooldown(); //Only add the cooldown

            if (mob != null && !mob.isDead()) {
                getPlayer().getWorld().spawnParticle(Particle.SMOKE_LARGE, mob.getLocation(), 5, 0.6, 0.6, 0.6, 0);
                mob.remove();
                SHIELD_MOBS.remove(mob);
            }
        }
    }

    @Override
    public long getCooldownLength() {
        return 10_000L;
    }

    @Override
    public CustomItem getItem() {
        return CustomItemRegistry.RUSHIA_SHIELD;
    }

    public Mob getShieldMob() {
        return mob;
    }

    public static Set<Mob> getShieldMobs() {
        return SHIELD_MOBS;
    }
}
