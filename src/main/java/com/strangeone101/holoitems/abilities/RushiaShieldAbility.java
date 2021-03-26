package com.strangeone101.holoitems.abilities;

import com.strangeone101.holoitems.Keys;
import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitemsapi.ItemAbility;
import com.strangeone101.holoitems.HoloItemsPlugin;
import com.strangeone101.holoitems.items.Items;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Ageable;
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
import org.bukkit.scheduler.BukkitRunnable;
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

        if (stack.getItemMeta().getPersistentDataContainer().get(Keys.getKeys().RUSHIA_SHIELD_COUNT, PersistentDataType.INTEGER) > 0) {
            start();
        }
    }

    @Override
    public void tick() {
        if (!getPlayer().isHandRaised() && !mobSpawned) { //isHandRaised is for when the shield is being raised. isBlocking only returns true
            remove();                      //when the shield is FULLY up.
            return;
        }

        if (!getPlayer().isBlocking() && !mobSpawned) { //If the shield isn't fully up yet
            Location target = getPlayer().getEyeLocation().add(getPlayer().getEyeLocation().getDirection().clone().multiply(1.5));
            getPlayer().getWorld().spawnParticle(Particle.SMOKE_LARGE, target, 5, 0.6, 0.6, 0.6, 0);
            return;
        }

        if (!mobSpawned) { //If the shield is fully up but the mob hasn't spawned yet
            Location target = getPlayer().getLocation().add(getPlayer().getLocation().getDirection().clone().multiply(1.5));
            String mobString = getStack().getItemMeta().getPersistentDataContainer().get(Keys.getKeys().RUSHIA_SHIELD_MOBS, PersistentDataType.STRING);
            String mobToUse = mobString.split(";")[0];

            if (mobToUse.equals("")) { //Something went really wrong!
                remove();
                return;
            }

            mobString = mobString.contains(";") ? mobString.split(";", 2)[1] : ""; //Get the deepest mob
            int count = mobString.split(";").length;
            EntityType type = EntityType.valueOf(mobToUse);

            mob = (Mob) getPlayer().getWorld().spawnEntity(target, type);
            SHIELD_MOBS.add(mob);
            mobSpawned = true;
            if (GLOW_MODE) {
                mob.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 99999, 0, true, false));
                mob.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 0, true, false));
            }
            mob.setFallDistance(0);
            mob.getEquipment().clear(); //Make sure they have no equipment
            if (mob.getHealth() > 25) mob.setHealth(25);
            if (mob instanceof Ageable) ((Ageable) mob).setAdult(); //No baby zombies

            //Get all mobs within 15 blocks and if they are targeting the player, make them target the new shield entity
            for (Entity entity : getPlayer().getWorld().getNearbyEntities(getPlayer().getLocation(), 15, 15, 15)) {
                if (entity instanceof Mob) {
                    if (((Mob) entity).getTarget() == getPlayer()) {
                        ((Mob) entity).setTarget(mob);
                    }
                }
            }

            ItemMeta meta = getStack().getItemMeta();
            meta.getPersistentDataContainer().set(Keys.getKeys().RUSHIA_SHIELD_MOBS, PersistentDataType.STRING, mobString);
            meta.getPersistentDataContainer().set(Keys.getKeys().RUSHIA_SHIELD_COUNT, PersistentDataType.INTEGER, count);
            getStack().setItemMeta(meta);

            getItem().updateStack(getStack(), getPlayer());

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
            target.subtract(0, mob.getEyeHeight() / 2, 0);  //This makes mobs always be "centered" on the Y axis, so they
                                                                    //aren't above or bellow the target location because of their height

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

            mob.setVelocity(vector); //Move the mob
        }
    }

    @Override
    public void remove() {
        super.remove();

        if (mobSpawned) {
            applyCooldown(); //Only add the cooldown

            if (mob != null && !mob.isDead()) {
                getPlayer().getWorld().spawnParticle(Particle.SMOKE_LARGE, mob.getLocation(), 12, 0.6, 0.6, 0.6, 0);
                mob.remove();
                SHIELD_MOBS.remove(mob);
            }

            //Play a sound to let them know when the cooldown is up. Can be changed to a message
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (getPlayer() != null && getPlayer().isOnline()) { //They didn't log out in the time it takes to run
                        getPlayer().playSound(getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
                    }
                }
            }.runTaskLater(HoloItemsPlugin.INSTANCE, getCooldownRemaining() / 50); //Convert to ticks
        }
    }

    @Override
    public long getCooldownLength() {
        return 10_000L;
    }

    @Override
    public CustomItem getItem() {
        return Items.RUSHIA_SHIELD;
    }

    public Mob getShieldMob() {
        return mob;
    }

    public static Set<Mob> getShieldMobs() {
        return SHIELD_MOBS;
    }
}
