package com.strangeone101.holoitems.listener;

import com.strangeone101.holoitems.CustomItem;
import com.strangeone101.holoitems.CustomItemRegistry;
import com.strangeone101.holoitems.HoloItemsPlugin;
import com.strangeone101.holoitems.Properties;
import com.strangeone101.holoitems.Property;
import com.strangeone101.holoitems.abilities.BerryTridentAbility;
import com.strangeone101.holoitems.abilities.RushiaShieldAbility;
import com.strangeone101.holoitems.items.EnchantedBlock;
import com.strangeone101.holoitems.items.Items;
import com.strangeone101.holoitems.items.RushiaShield;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Boss;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

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
                    if (customItem == Items.RUSHIA_SHIELD && !RushiaShieldAbility.getShieldMobs().contains(event.getEntity())
                            && !RushiaShield.EXCEPTIONS.contains(event.getEntity().getType())) {
                        ((RushiaShield)customItem).killMob((Mob) event.getEntity(), event.getEntity().getKiller(), item);
                        killer.getInventory().setItem(slot, item); //Update the itemstack because it's not updated when accessed through getItem(slot)
                        return;
                    }
                }
            }
        }

        if (event.getEntity().getType() == EntityType.ENDERMITE) {

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        CustomItem ci = CustomItemRegistry.getCustomItem(event.getItemInHand());

        if (ci != null) {
            if (ci instanceof EnchantedBlock) {
                if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    final ItemStack stack = event.getItemInHand().clone();
                    final EquipmentSlot slot = event.getHand();
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (event.getPlayer().getEquipment().getItem(slot) == null ||
                                    event.getPlayer().getEquipment().getItem(slot).getType() == Material.AIR) {
                                event.getPlayer().getEquipment().setItem(slot, stack);
                            } else {
                                event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), stack);
                            }
                        }
                    }.runTaskLater(HoloItemsPlugin.INSTANCE, 1L);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onContainerSteal(org.bukkit.event.inventory.InventoryMoveItemEvent event) {
        CustomItem item = CustomItemRegistry.getCustomItem(event.getItem());
        if (item != null) {
            if (item instanceof EnchantedBlock) {
                event.setCancelled(true);
                event.getDestination().addItem(new ItemStack(event.getItem().getType()));
            }
        }
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        CustomItem customItem = CustomItemRegistry.getCustomItem(event.getItem());
        int slot = event.getHand() == EquipmentSlot.OFF_HAND ? 40 : event.getPlayer().getInventory().getHeldItemSlot();

        if (customItem != null) {
            if (customItem == Items.RUSHIA_SHIELD) {
                new RushiaShieldAbility(event.getPlayer(), event.getItem(), event.getPlayer().getInventory(), slot);
            } else if (customItem == Items.BERRY_TRIDENT) {
                new BerryTridentAbility(event.getPlayer(), event.getItem(), event.getPlayer().getInventory(), slot);
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

        if (!HoloItemsPlugin.recipes.containsValue(event.getRecipe())) {
            for (ItemStack ingredient : event.getInventory().getMatrix()) {
                if (CustomItemRegistry.isCustomItem(ingredient)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPreCraft(PrepareItemCraftEvent event) {
        if (!HoloItemsPlugin.recipes.containsValue(event.getRecipe())) {
            for (ItemStack ingredient : event.getInventory().getMatrix()) {
                if (CustomItemRegistry.isCustomItem(ingredient)) {
                    event.getInventory().setResult(null); //Stops recipes using our custom items
                }
            }
        }
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        //For villager inventories and grindstone inventories.
        if (event.getClickedInventory() instanceof MerchantInventory
                || event.getClickedInventory() instanceof GrindstoneInventory) {
            if (event.getRawSlot() == 2) { //Output slot
                //If any of the inputs are custom items
                for (int slot = 0; slot < 1; slot++) {
                    ItemStack stack = event.getClickedInventory().getItem(slot);
                    if (stack == null) continue;
                    if (CustomItemRegistry.isCustomItem(stack)) {

                        if (event.getClickedInventory() instanceof MerchantInventory) {
                            List<ItemStack> ingre = ((MerchantInventory)event.getClickedInventory()).getSelectedRecipe().getIngredients();
                            //If the ingredient slot we are getting isnt null
                            if (ingre.size() > slot) {
                                //If the item in the recipe isnt a custom item OR if it doesnt match, set output to null
                                if (!CustomItemRegistry.isCustomItem(ingre.get(slot)) ||
                                           CustomItemRegistry.getCustomItem(stack) != CustomItemRegistry.getCustomItem(ingre.get(slot))) {
                                    event.setCancelled(true); //Deny it
                                    event.getClickedInventory().setItem(2, null); //Set the output to null again
                                }
                            }
                        }

                        event.setCancelled(true); //Deny it
                        event.getClickedInventory().setItem(2, null); //Set the output to null again
                    }
                }

            } else if (event.getRawSlot() < 3 && CustomItemRegistry.isCustomItem(event.getCurrentItem())) {
                event.getClickedInventory().setItem(2, null); //Try set the output to null
            }
        } else if (event.getClickedInventory() instanceof AnvilInventory) {
            if (event.getRawSlot() == 1 && CustomItemRegistry.isCustomItem(event.getCurrentItem())) {
                event.getClickedInventory().setItem(2, null); //Set the output to null again
            }

            
        }
    }

    @EventHandler
    public void onTradeSelect(TradeSelectEvent event) {
        for (int slot = 0; slot < 1; slot++) {
            ItemStack stack = event.getInventory().getItem(slot);
            if (stack == null) continue;
            if (CustomItemRegistry.isCustomItem(stack)) {
                List<ItemStack> ingre = ((MerchantInventory)event.getInventory()).getSelectedRecipe().getIngredients();
                //If the ingredient slot we are getting isnt null
                if (ingre.size() > slot) {
                    //If the item in the recipe isnt a custom item OR if it doesnt match, set output to null
                    if (!CustomItemRegistry.isCustomItem(ingre.get(slot)) ||
                            CustomItemRegistry.getCustomItem(stack) != CustomItemRegistry.getCustomItem(ingre.get(slot))) {
                        event.setCancelled(true); //Deny it
                        event.getInventory().setItem(2, null); //Set the output to null again
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBrewRefuel(BrewingStandFuelEvent event) {
        if (CustomItemRegistry.isCustomItem(event.getFuel())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRenamePre(PrepareAnvilEvent event) {
        if (event.getInventory().getItem(1) != null) {
            if (CustomItemRegistry.isCustomItem(event.getInventory().getItem(1))) {
                event.setResult(null);
            }
        }
        if (event.getInventory().getItem(0) != null) {
            CustomItem item = CustomItemRegistry.getCustomItem(event.getInventory().getItem(0));

            if (item != null) {
                //Cancel if it can't be renamed
                if (!item.getProperties().contains(Properties.RENAMED)) {
                    event.setResult(null);
                }
            }
        }
    }
}
