package com.strangeone101.holoitems.listener;

import com.strangeone101.holoitems.CustomItem;
import com.strangeone101.holoitems.CustomItemRegistry;
import com.strangeone101.holoitems.HoloItemsPlugin;
import com.strangeone101.holoitems.Properties;
import com.strangeone101.holoitems.abilities.BerryTridentAbility;
import com.strangeone101.holoitems.abilities.RushiaShieldAbility;
import com.strangeone101.holoitems.items.EnchantedBlock;
import com.strangeone101.holoitems.items.Items;
import com.strangeone101.holoitems.items.MoguBoots;
import com.strangeone101.holoitems.items.RushiaShield;
import com.strangeone101.holoitems.items.RushianRevolver;
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
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ItemListener implements Listener {

    public static final HashSet<Material> INTERACTABLES = new HashSet<>();

    public ItemListener() {
        INTERACTABLES.addAll(Arrays.asList(
                Material.CARTOGRAPHY_TABLE, Material.ENCHANTING_TABLE, Material.SMITHING_TABLE, Material.CRAFTING_TABLE,
                Material.LOOM, Material.STONECUTTER, Material.GRINDSTONE, Material.BREWING_STAND,
                Material.CHEST, Material.TRAPPED_CHEST, Material.DISPENSER, Material.DROPPER, Material.BARREL,
                Material.HOPPER, Material.FURNACE, Material.BLAST_FURNACE, Material.SMOKER,
                Material.ENDER_CHEST, Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL, Material.COMMAND_BLOCK,
                Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.LEVER, Material.COMPARATOR,
                Material.REPEATER, Material.NOTE_BLOCK, Material.DAYLIGHT_DETECTOR, Material.LECTERN, Material.OAK_DOOR,
                Material.OAK_FENCE_GATE, Material.OAK_TRAPDOOR, Material.OAK_BUTTON, Material.SPRUCE_DOOR, Material.SPRUCE_TRAPDOOR,
                Material.SPRUCE_FENCE_GATE, Material.SPRUCE_BUTTON, Material.BIRCH_DOOR, Material.BIRCH_TRAPDOOR,
                Material.BIRCH_FENCE_GATE, Material.BIRCH_BUTTON, Material.JUNGLE_DOOR, Material.JUNGLE_TRAPDOOR,
                Material.JUNGLE_FENCE_GATE, Material.JUNGLE_BUTTON, Material.ACACIA_DOOR, Material.ACACIA_TRAPDOOR,
                Material.ACACIA_FENCE_GATE, Material.ACACIA_BUTTON, Material.DARK_OAK_DOOR, Material.DARK_OAK_TRAPDOOR,
                Material.DARK_OAK_FENCE_GATE, Material.DARK_OAK_BUTTON, Material.WARPED_DOOR, Material.WARPED_FENCE_GATE,
                Material.WARPED_TRAPDOOR, Material.WARPED_BUTTON, Material.CRIMSON_DOOR, Material.CRIMSON_FENCE_GATE,
                Material.CRIMSON_TRAPDOOR, Material.CRIMSON_BUTTON, Material.STONE_BUTTON, Material.POLISHED_BLACKSTONE_BUTTON,
                Material.SHULKER_BOX, Material.BLACK_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX,
                Material.CYAN_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX,
                Material.LIGHT_GRAY_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX,
                Material.ORANGE_SHULKER_BOX, Material.PINK_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.RED_SHULKER_BOX,
                Material.YELLOW_SHULKER_BOX, Material.WHITE_SHULKER_BOX
        ));
    }

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
            } else event.setCancelled(true);
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

        boolean isInteractable = event.getClickedBlock() != null && INTERACTABLES.contains(event.getClickedBlock().getType()) && !event.getPlayer().isSneaking();

        if (isInteractable) {
            return; //Don't cancel the event because they opened some form of GUI
        }

        if (customItem != null) {
            if (customItem == Items.RUSHIA_SHIELD) {
                new RushiaShieldAbility(event.getPlayer(), event.getItem(), event.getPlayer().getInventory(), slot);
            } else if (customItem == Items.BERRY_TRIDENT) {
                new BerryTridentAbility(event.getPlayer(), event.getItem(), event.getPlayer().getInventory(), slot);
            } else if (customItem == Items.RUSSIAN_ROULETTE_REVOLVER) {
                RushianRevolver.fire(event.getPlayer(), event.getItem(), event.getPlayer().isSneaking());
            }

            if (customItem != Items.RUSHIA_SHIELD) {
                event.setCancelled(true); //Prevent interactions of every other custom item
            }


        }
    }

    @EventHandler
    public void onInvPickup(InventoryPickupItemEvent event) {
        ItemStack stack = event.getItem().getItemStack();
        CustomItem item = CustomItemRegistry.getCustomItem(stack);

        if (item != null) {
            stack = item.updateStack(stack, event.getInventory().getHolder() instanceof Player ? (Player)event.getInventory().getHolder() : null);
            event.getItem().setItemStack(stack);
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        ItemStack stack = event.getEntity().getItemStack();
        CustomItem item = CustomItemRegistry.getCustomItem(stack);

        if (item != null) {
            stack = item.updateStack(stack, null);
            event.getEntity().setItemStack(stack);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlock() != event.getTo().getBlock()) {
            CustomItem item = CustomItemRegistry.getCustomItem(event.getPlayer().getEquipment().getBoots());
            if (item == Items.MOGU_BOOTS && event.getPlayer().isOnGround()) {
                MoguBoots.mogu(event.getPlayer().getLocation());
            }
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
                || event.getClickedInventory() instanceof GrindstoneInventory
                || event.getClickedInventory() instanceof AnvilInventory) {
            boolean prevent = shouldPrevent(event.getClickedInventory());
            if (event.getRawSlot() == 2 && prevent) { //Output slot
                //If any of the inputs are custom items
                event.setCancelled(true); //Deny it
                event.getClickedInventory().setItem(2, null); //Set the output to null again
            } else if (prevent) {
                event.getClickedInventory().setItem(2, null); //Set the output to null again
                updateLater(event.getClickedInventory());
            } else if (event.getRawSlot() < 2 || event.getClick().isShiftClick()) {
                updateLater(event.getClickedInventory());
            } else if (event.getRawSlot() == 2 && event.getClickedInventory() instanceof AnvilInventory
                    && CustomItemRegistry.isCustomItem(event.getClickedInventory().getItem(2))) {
                ItemStack stack = event.getClickedInventory().getItem(2);
                ItemMeta meta = stack.getItemMeta();
                Properties.RENAMED.set(meta.getPersistentDataContainer(), 1); //Mark it as renamed
                stack.setItemMeta(meta);
                event.getClickedInventory().setItem(2, stack); //Set the updated item back into the inventory
            }

        }
    }

    @EventHandler
    public void onTradeSelect(TradeSelectEvent event) {
        List<ItemStack> ingre = ((MerchantInventory)event.getInventory()).getSelectedRecipe().getIngredients();
        for (int slot = 0; slot < 1 && slot < ingre.size() - 1; slot++) {
            ItemStack stack = event.getInventory().getItem(slot);
            if (stack == null) continue;
            if (areStacksMismatched(ingre.get(slot), stack)) {
                event.getInventory().setItem(2, null); //Set the output to null again
                updateLater(event.getInventory());
            }
        }
    }

    public boolean areStacksMismatched(ItemStack stack1, ItemStack stack2) {
        CustomItem ci1 = CustomItemRegistry.getCustomItem(stack1);
        CustomItem ci2 = CustomItemRegistry.getCustomItem(stack2);

        return (ci1 != ci2 && !(ci1 == null && ci2 == null)) //if they mismatch but both aren't null
                || ci1.getDurability(stack1) != ci2.getDurability(stack2); //or if they have mismatched durability
    }

    public boolean shouldPrevent(Inventory inventory) {
        if (inventory instanceof GrindstoneInventory) {
            ItemStack slot1 = inventory.getItem(0);
            ItemStack slot2 = inventory.getItem(1);
            if ((CustomItemRegistry.isCustomItem(slot1))
                    || CustomItemRegistry.isCustomItem(slot2)) {
                return true; //Block disenchant of custom items
            }
            return false;
        } else if (inventory instanceof AnvilInventory) {
            ItemStack slot1 = inventory.getItem(0);
            ItemStack slot2 = inventory.getItem(1);

            if (CustomItemRegistry.isCustomItem(slot2)) {
                return true; //Prevent custom items from being used to repair
            } else if (slot2 != null && CustomItemRegistry.isCustomItem(slot1)) {
                return true; //Prevent repair of custom items
            } else if (CustomItemRegistry.isCustomItem(slot1)) {
                CustomItem ci = CustomItemRegistry.getCustomItem(slot1);
                if (!ci.getProperties().contains(Properties.RENAMED)) {
                    return true; //This item cannot be renamed
                }
            }
            return false;
        } else if (inventory instanceof MerchantInventory) {
            List<ItemStack> ingre = ((MerchantInventory)inventory).getSelectedRecipe().getIngredients();
            for (int slot = 0; slot < 1 && slot < ingre.size() - 1; slot++) {
                ItemStack stack = inventory.getItem(slot);
                if (stack == null) continue;
                if (areStacksMismatched(ingre.get(slot), stack)) {
                    return true; //Trades dont match for our custom items
                }
            }
            return false;
        }
        return false;
    }

    public void updateLater(Inventory inventory) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (shouldPrevent(inventory)) inventory.setItem(2, null);
            }
        };
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
