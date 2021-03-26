package com.strangeone101.holoitems.items.abilities;

import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitemsapi.CustomItemRegistry;
import com.strangeone101.holoitems.HoloItemsPlugin;
import com.strangeone101.holoitemsapi.ItemAbility;
import com.strangeone101.holoitems.items.interfaces.Edible;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FoodAbility extends ItemAbility {

    private CustomItem item;
    private boolean vanillaFood = false; //TODO
    private boolean offHand;
    private int tick;

    public FoodAbility(Player player, ItemStack stack, Inventory inv, int slot) {
        super(player, stack, inv, slot);
        this.item = CustomItemRegistry.getCustomItem(stack);
        this.offHand = slot == 40;

        if (this.item instanceof Edible && !ItemAbility.isAbilityActive(player, this.getClass())) {
            if (player.getFoodLevel() < 20 || ((Edible)item).eatWhenFull()) {
                start();
                vanillaFood = stack.getType().isEdible();
            }
        }
    }


    @Override
    public void tick() {
        EquipmentSlot slot = offHand ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;
        if (CustomItemRegistry.getCustomItem(getPlayer().getInventory().getItem(slot)) != item) {
            remove();
            return;
        }

        if (getStartTime() + ((Edible)item).getEatDuration() < System.currentTimeMillis()) {
            getStack().setAmount(getStack().getAmount() - 1);
            if (getStack().getAmount() == 0) {
                getStack().setType(Material.AIR);
            }


            Edible edible = (Edible) item;

            //Using Math.min to make sure the amount doesn't exceed 20
            getPlayer().setFoodLevel(Math.min(getPlayer().getFoodLevel() + edible.getHungerAmount(), 20));
            getPlayer().setSaturation(Math.min(getPlayer().getSaturation() + edible.getSaturationAmount(), 20));

            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);

            if (vanillaFood) {
                ItemStack clone = getStack().clone();
                getStack().setType(Material.AIR);
                getStack().setAmount(0);
                getPlayer().getInventory().setItem(slot, getStack());
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        getPlayer().getInventory().setItem(slot, clone);
                    }
                }.runTaskLater(HoloItemsPlugin.INSTANCE, 1L);
            }
            remove();
            return;
        }

        tick++;

        //Only play particles every second tick
        if (tick % 2 == 0) {
            //Only play sound every 4 ticks
            if (tick % 4 == 0) getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);
            Vector vector = getPlayer().getLocation().getDirection().clone();
            vector.setY(0); //Ignore their yaw
            vector.normalize().multiply(0.25); //Get 0.15 blocks in front of the players face
            vector.setY(1.6); //Add 1.6 blocks so the height is their face
            getPlayer().getWorld().spawnParticle(Particle.ITEM_CRACK, getPlayer().getLocation().add(vector), 2, 0.125, 0.05, 0.125, 0, getStack());
        }
    }

    @Override
    public void remove() {
        super.remove();


    }

    @Override
    public long getCooldownLength() {
        return 0;
    }

    @Override
    public CustomItem getItem() {
        return item;
    }
}
