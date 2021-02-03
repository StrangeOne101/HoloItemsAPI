package com.strangeone101.holoitems.listener;

import com.strangeone101.holoitems.CustomItem;
import com.strangeone101.holoitems.CustomItemRegistry;
import com.strangeone101.holoitems.HoloItemsPlugin;
import com.strangeone101.holoitems.abilities.RushiaShieldAbility;
import com.strangeone101.holoitems.items.RushiaShield;
import org.bukkit.GameMode;
import org.bukkit.entity.Boss;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null && event.getEntity() instanceof Mob && !(event.getEntity() instanceof Boss)) { //If the entity WAS killed by a player
            Player killer = event.getEntity().getKiller();

            //Cycle through the offhand and normal hand for
            for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.OFF_HAND, EquipmentSlot.HAND}) {
                ItemStack item = killer.getInventory().getItem(slot);
                CustomItem customItem = CustomItemRegistry.getCustomItem(item);

                if (customItem != null) {
                    if (customItem == CustomItemRegistry.RUSHIA_SHIELD && !RushiaShieldAbility.getShieldMobs().contains(event.getEntity())
                            && !RushiaShield.EXCEPTIONS.contains(event.getEntity().getType())) {
                        ((RushiaShield)customItem).killMob((Mob) event.getEntity(), event.getEntity().getKiller(), item);
                        killer.getInventory().setItem(slot, item); //Update the itemstack because it's not updated when accessed through getItem(slot)
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        CustomItem customItem = CustomItemRegistry.getCustomItem(event.getItem());
        int slot = event.getHand() == EquipmentSlot.OFF_HAND ? 40 : event.getPlayer().getInventory().getHeldItemSlot();

        if (customItem != null) {
            if (customItem == CustomItemRegistry.RUSHIA_SHIELD) {
                new RushiaShieldAbility(event.getPlayer(), event.getItem(), event.getPlayer().getInventory(), slot);
            }
        }
    }

    @EventHandler
    public void onPickup(InventoryPickupItemEvent event) {
        ItemStack stack = event.getItem().getItemStack();
        CustomItem item = CustomItemRegistry.getCustomItem(stack);

        if (item != null) {
            item.updateStack(stack, event.getInventory().getHolder() instanceof Player ? (Player)event.getInventory().getHolder() : null);
            event.getItem().setItemStack(stack);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack stack = event.getCurrentItem();
        if (CustomItemRegistry.isCustomItem(event.getCurrentItem()) && event.getWhoClicked() instanceof Player) {
            //Makes the output a fresh build of the item. Means it will be owned by that player
            event.setCurrentItem(CustomItemRegistry.getCustomItem(stack).buildStack((Player) event.getWhoClicked()));
        }
    }
}
