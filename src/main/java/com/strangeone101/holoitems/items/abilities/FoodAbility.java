package com.strangeone101.holoitems.items.abilities;

import com.strangeone101.holoitems.CustomItem;
import com.strangeone101.holoitems.CustomItemRegistry;
import com.strangeone101.holoitems.ItemAbility;
import com.strangeone101.holoitems.items.interfaces.Edible;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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

        if (!(this.item instanceof Edible)) {
            start();

            vanillaFood = stack.getType().isEdible();


        }
    }


    @Override
    public void tick() {
        EquipmentSlot slot = offHand ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;
        if (CustomItemRegistry.getCustomItem(getPlayer().getInventory().getItem(slot)) != item) {
            remove();
            return;
        }

        if (getStartTime() > ((Edible)item).getEatDuration()) {
            getStack().setAmount(getStack().getAmount());
            if (getStack().getAmount() == 0) {
                getStack().setType(Material.AIR);
            }

            Edible edible = (Edible) item;

            //Using Math.min to make sure the amount doesn't exceed 20
            getPlayer().setFoodLevel(Math.min(getPlayer().getFoodLevel() + edible.getHungerAmount(), 20));
            getPlayer().setSaturation(Math.min(getPlayer().getSaturation() + edible.getSaturationAmount(), 20));

            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);

            remove();
            return;
        }

        tick++;

        if (tick % 2 == 0) {
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);
            Vector vector = getPlayer().getLocation().getDirection().clone();
            vector.setY(0); //Ignore their yaw
            vector.normalize().multiply(0.15); //Get 0.15 blocks in front of the players face
            vector.setY(1.6); //Add 1.6 blocks so the height is their face
            getPlayer().getWorld().spawnParticle(Particle.ITEM_CRACK, getPlayer().getLocation().add(vector), 2, 0.25, 0.1, 0.25, getStack());
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
