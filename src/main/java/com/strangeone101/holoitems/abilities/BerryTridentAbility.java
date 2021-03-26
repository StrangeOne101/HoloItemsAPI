package com.strangeone101.holoitems.abilities;

import com.strangeone101.holoitems.Keys;
import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitems.HoloItemsPlugin;
import com.strangeone101.holoitemsapi.ItemAbility;
import com.strangeone101.holoitems.items.Items;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class BerryTridentAbility extends ItemAbility {

    public static Set<ArmorStand> STANDS = new HashSet<>();

    private boolean spawned = false;
    private boolean backwards = false;
    private ArmorStand stand;
    private Vector lastVelocity;

    public BerryTridentAbility(Player player, ItemStack stack, Inventory inventory, int slot) {
        super(player, stack, inventory, slot);

        if (ItemAbility.isAbilityActive(player, this.getClass())) return; //Ability already active or on cooldown

        if (stack.getItemMeta().getPersistentDataContainer().get(Keys.getKeys().BERRY_TRIDENT_THROWN, PersistentDataType.INTEGER) == 0) {
            start();
        }
    }

    @Override
    public void tick() {
        if (!spawned) {
            spawned = true;

            Location target = getPlayer().getEyeLocation().add(getPlayer().getEyeLocation().getDirection().clone().multiply(1.5));
            target.setDirection(getPlayer().getEyeLocation().getDirection());
            stand = getPlayer().getWorld().spawn(target, ArmorStand.class);
            stand.setBasePlate(false);
            stand.setInvisible(true);
            stand.setHeadPose(new EulerAngle(180, 0, 0));
            stand.getEquipment().setHelmet(getClonedStack(getStack()));


            STANDS.add(stand);

            lastVelocity = getPlayer().getEyeLocation().getDirection().clone().multiply(15);
            stand.setVelocity(lastVelocity);

            ItemMeta meta = getStack().getItemMeta();
            meta.getPersistentDataContainer().set(Keys.getKeys().BERRY_TRIDENT_THROWN, PersistentDataType.INTEGER, 1);
            getStack().setItemMeta(meta);
            return;
        }

        if (stand.getVelocity().angle(lastVelocity) > 30 && !backwards) {
            //TODO HIT
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ITEM_TRIDENT_HIT_GROUND, 1, 1.5F);
            backwards = true;
            stand.setVelocity(new Vector(0, 0, 0));
        }

        if (!backwards) {
            for (Entity e : getPlayer().getWorld().getNearbyEntities(stand.getEyeLocation(), 0.6, 0.6, 0.6)) {
                if (e instanceof Damageable) {
                    //TODO Damage mob with custom damage
                    getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ITEM_TRIDENT_HIT, 1, 1.5F);
                    backwards = true;
                }
            }
        }
    }



    @Override
    public long getCooldownLength() {
        return 0;
    }

    @Override
    public CustomItem getItem() {
        return Items.BERRY_TRIDENT;
    }

    @Override
    public void remove() {
        super.remove();

        if (spawned) {
            if (stand != null && !stand.isDead()) {
                stand.remove();
                STANDS.remove(stand);
            }
        }
    }

    public static ItemStack getClonedStack(ItemStack stack) {
        ItemStack newStack = stack.clone();
        ItemMeta meta = newStack.getItemMeta();
        meta.setCustomModelData(stack.getItemMeta().getCustomModelData() + 1);
        newStack.setItemMeta(meta);
        return newStack;
    }
}
