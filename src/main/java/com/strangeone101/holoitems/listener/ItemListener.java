package com.strangeone101.holoitems.listener;

import com.strangeone101.holoitems.CustomItem;
import com.strangeone101.holoitems.CustomItemRegistry;
import com.strangeone101.holoitems.HoloItemsPlugin;
import com.strangeone101.holoitems.Properties;
import com.strangeone101.holoitems.items.Items;
import com.strangeone101.holoitems.items.abilities.FoodAbility;
import com.strangeone101.holoitems.items.implementations.MoguBoots;
import com.strangeone101.holoitems.items.interfaces.BlockInteractable;
import com.strangeone101.holoitems.items.interfaces.Edible;
import com.strangeone101.holoitems.items.interfaces.EntityInteractable;
import com.strangeone101.holoitems.items.interfaces.Interactable;
import com.strangeone101.holoitems.items.interfaces.Placeable;
import com.strangeone101.holoitems.items.interfaces.Swingable;
import com.strangeone101.holoitems.util.ItemUtils;
import com.strangeone101.holoitems.EventContext;
import org.bukkit.Material;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;

public class ItemListener implements Listener {

    public static final HashSet<Material> INTERACTABLES = new HashSet<>();

    public static final HashSet<EntityType> GREENLIT_ENTITY_INTERACTIONS = new HashSet<>();
    public static final HashMap<EntityType, BiFunction<Entity, Material, Boolean>> ENTITY_INTERACTABLES = new HashMap<>();

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

        GREENLIT_ENTITY_INTERACTIONS.addAll(Arrays.asList(EntityType.VILLAGER, EntityType.MINECART, EntityType.BOAT,
                EntityType.ARMOR_STAND, EntityType.ITEM_FRAME, EntityType.WANDERING_TRADER, EntityType.MINECART_CHEST,
                EntityType.MINECART_HOPPER));

        ENTITY_INTERACTABLES.put(EntityType.PIG, (entity, item) -> !(item == Material.CARROT || item == Material.SADDLE
                || item == Material.LEAD || item == Material.NAME_TAG));
        ENTITY_INTERACTABLES.put(EntityType.STRIDER, (entity, item) -> !(item == Material.WARPED_FUNGUS
                || item == Material.LEAD || item == Material.NAME_TAG));
        ENTITY_INTERACTABLES.put(EntityType.HORSE, (entity, item) -> !(item == Material.WHEAT || item == Material.HAY_BLOCK
                || item == Material.APPLE || item == Material.SUGAR || item == Material.GOLDEN_CARROT || item == Material.GOLDEN_APPLE
                || item == Material.LEAD || item == Material.ENCHANTED_GOLDEN_APPLE || item == Material.GOLDEN_HORSE_ARMOR
                || item == Material.IRON_HORSE_ARMOR || item == Material.DIAMOND_HORSE_ARMOR || item == Material.LEATHER_HORSE_ARMOR
                || item == Material.SADDLE || item == Material.NAME_TAG));
        ENTITY_INTERACTABLES.put(EntityType.DONKEY, (entity, item) -> !(item == Material.WHEAT || item == Material.HAY_BLOCK
                || item == Material.APPLE || item == Material.SUGAR || item == Material.GOLDEN_CARROT || item == Material.GOLDEN_APPLE
                || item == Material.LEAD || item == Material.ENCHANTED_GOLDEN_APPLE || item == Material.CHEST
                || item == Material.SADDLE || item == Material.NAME_TAG));
        ENTITY_INTERACTABLES.put(EntityType.MULE, ENTITY_INTERACTABLES.get(EntityType.DONKEY));
        ENTITY_INTERACTABLES.put(EntityType.LLAMA, (entity, item) -> !(item == Material.LEAD || item == Material.WHEAT
                || item == Material.HAY_BLOCK || item == Material.CHEST || item.toString().endsWith("CARPET")
                || item == Material.NAME_TAG));
        ENTITY_INTERACTABLES.put(EntityType.SKELETON_HORSE, (entity, item) -> !(item == Material.SADDLE || item == Material.NAME_TAG));
        ENTITY_INTERACTABLES.put(EntityType.ZOMBIE_HORSE, ENTITY_INTERACTABLES.get(EntityType.SKELETON_HORSE));
        ENTITY_INTERACTABLES.put(EntityType.WOLF, (entity, item) -> (((Wolf)entity).isTamed()) && !(item == Material.BONE
                || ItemUtils.isDye(item) || item == Material.LEAD || ItemUtils.isMeat(item) || item == Material.ROTTEN_FLESH
                || item == Material.NAME_TAG));
        ENTITY_INTERACTABLES.put(EntityType.CAT, (entity, item) -> (((Cat)entity).isTamed()) && !(item == Material.LEAD
                || ItemUtils.isFish(item) || ItemUtils.isDye(item) || item == Material.NAME_TAG));
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (!EventContext.isCached(event.getPlayer())) {
            EventContext.fullCache(event.getPlayer());
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        //Release cache in 10 seconds after logout
        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.getPlayer() == null || !event.getPlayer().isOnline()) {
                    EventContext.release(event.getPlayer());
                }
            }
        }.runTaskLater(HoloItemsPlugin.INSTANCE, 20 * 10L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        CustomItem ci = CustomItemRegistry.getCustomItem(event.getItemInHand());

        if (ci != null) {
            if (ci instanceof Placeable) {
                Placeable placeable = (Placeable) ci;

                //If the place event according to the custom item should be stopped
                if (placeable.place(event.getBlock(), event.getPlayer(), ci, event.getItemInHand())) {
                    event.setCancelled(true);
                }
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemUse(PlayerInteractEvent event) {

        CustomItem customItem = CustomItemRegistry.getCustomItem(event.getItem());
        int slot = event.getHand() == EquipmentSlot.OFF_HAND ? 40 : event.getPlayer().getInventory().getHeldItemSlot();

        boolean isInteractable = event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null
                && INTERACTABLES.contains(event.getClickedBlock().getType()) && !event.getPlayer().isSneaking();

        if (customItem != null) {

            //Run BlockInteractables before GUI checks
            if (customItem instanceof BlockInteractable && event.getClickedBlock() != null) {
                if (((BlockInteractable)customItem).onInteract(event.getPlayer(), event.getClickedBlock(), customItem,
                        event.getItem(), event.getAction() == Action.LEFT_CLICK_BLOCK)) {
                    event.setUseInteractedBlock(Event.Result.DENY);
                    //event.setCancelled(true);
                }
                return;
            }

            if (isInteractable) {
                return; //Don't cancel the event because they opened some form of GUI
            }

            if (customItem instanceof Placeable) {
                return; //Don't cancel placeable items since they are handled in the place event
            }

            if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
                if (customItem instanceof Interactable) {
                    if (((Interactable)customItem).onInteract(event.getPlayer(), customItem, event.getItem())) {
                        event.setUseItemInHand(Event.Result.DENY);
                        //event.setCancelled(true);

                    }
                    return;
                }

                if (customItem instanceof Edible) {
                    event.setUseItemInHand(Event.Result.DENY);
                    new FoodAbility(event.getPlayer(), event.getItem(), event.getPlayer().getInventory(), 0);
                    return;
                }

            } else if (customItem instanceof Swingable && (event.getAction() == Action.LEFT_CLICK_AIR
                    || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
                 ((Swingable)customItem).swing(event.getPlayer(), customItem, event.getItem());
                 return;
            }

            event.setUseItemInHand(Event.Result.DENY);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        CustomItem customItem = CustomItemRegistry.getCustomItem(event.getPlayer().getInventory().getItem(event.getHand()));

        if (customItem != null) {
            if (customItem instanceof EntityInteractable) {
                if (((EntityInteractable)customItem).onInteract(event.getRightClicked(), event.getPlayer(), customItem,
                        event.getPlayer().getInventory().getItem(event.getHand()))) {
                    event.setCancelled(true);
                }
                return;
            }

            EntityType type = event.getRightClicked().getType();
            Material mat = event.getPlayer().getInventory().getItem(event.getHand()).getType();

            //If it's not a greenlit entity and its not an entity that can be interacted with based on the item
            if (!(GREENLIT_ENTITY_INTERACTIONS.contains(type) ||
                    (ENTITY_INTERACTABLES.containsKey(type) && ENTITY_INTERACTABLES.get(type).apply(event.getRightClicked(), mat)))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvPickup(InventoryPickupItemEvent event) {
        ItemStack stack = event.getItem().getItemStack();
        CustomItem item = CustomItemRegistry.getCustomItem(stack);

        if (item != null) {
            stack = item.updateStack(stack, event.getInventory().getHolder() instanceof Player ? (Player)event.getInventory().getHolder() : null);
            event.getItem().setItemStack(stack);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickup(EntityPickupItemEvent event) {
        ItemStack stack = event.getItem().getItemStack();
        CustomItem item = CustomItemRegistry.getCustomItem(stack);

        if (item != null) {
            stack = item.updateStack(stack, event.getEntity() instanceof Player ? (Player)event.getEntity() : null);
            event.getItem().setItemStack(stack);

            if (event.getEntity() instanceof Player) {
                cacheLater((Player) event.getEntity());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSlotSwitch(PlayerItemHeldEvent event) {
        //Update the cached held item
        EventContext.swapCacheSlots(event.getPlayer(), event.getPreviousSlot(), event.getNewSlot(), event.getNewSlot());
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        ItemStack stack = event.getEntity().getItemStack();
        CustomItem item = CustomItemRegistry.getCustomItem(stack);

        if (item != null) {
            stack = item.updateStack(stack, null);
            event.getEntity().setItemStack(stack);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlock() != event.getTo().getBlock()) {
            CustomItem item = CustomItemRegistry.getCustomItem(event.getPlayer().getEquipment().getBoots());
            if (item == Items.MOGU_BOOTS && event.getPlayer().isOnGround()) {
                MoguBoots.mogu(event.getPlayer().getLocation());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
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

    @EventHandler(ignoreCancelled = true)
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

        event.getWhoClicked().sendMessage(event.getAction() + "," + event.getClick() + ","
                + event.getSlotType() + "," + event.getRawSlot() + "," + event.getSlot());

        if (event.getRawSlot() >= event.getInventory().getSize() && event.getWhoClicked() instanceof Player) { //If the click WASN'T in the top inventory
            if (event.getCursor() != null) {



                if (event.getCurrentItem().isSimilar(event.getCursor()) && event.getCurrentItem().getAmount() < event.getCurrentItem().getMaxStackSize()) {
                    EventContext.uncacheSlot((Player) event.getWhoClicked(), -1); //Delete the cached in item item
                } else {
                    EventContext.swapCacheSlots((Player) event.getWhoClicked(), -1, event.getSlot()); //Swap held item and the clicked slot
                }
                return;
            }

            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                EventContext.uncacheSlot((Player) event.getWhoClicked(), event.getSlot());
            }



            if (event.getAction() == InventoryAction.PICKUP_ALL || event.getAction() == InventoryAction.PICKUP_HALF ||
            event.getAction() == InventoryAction.PICKUP_ONE || event.getAction() == InventoryAction.PICKUP_SOME) {
                EventContext.updateCacheSlot((Player) event.getWhoClicked(), event.getSlot(), -1);
            } else if (event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_ONE ||
                    event.getAction() == InventoryAction.PLACE_SOME) {
                EventContext.updateCacheSlot((Player) event.getWhoClicked(), -1, event.getSlot());
            }

        }
    }

    @EventHandler(ignoreCancelled = true)
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
                return !ci.getProperties().contains(Properties.RENAMED); //This item cannot be renamed
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
        new BukkitRunnable() {
            @Override
            public void run() {
                if (shouldPrevent(inventory)) inventory.setItem(2, null);
            }
        }.runTaskLater(HoloItemsPlugin.INSTANCE, 1L);
    }

    public void cacheLater(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                EventContext.fullCache(player);
            }
        }.runTaskLater(HoloItemsPlugin.INSTANCE, 1L);
    }

    @EventHandler(ignoreCancelled = true)
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

    @EventHandler(ignoreCancelled = true)
    public void onDamageItem(PlayerItemDamageEvent event) {
        CustomItem item = CustomItemRegistry.getCustomItem(event.getItem());

        if (item != null) {
            if (item.getMaxDurability() > 0) {
                item.damageItem(event.getItem(), event.getDamage(), event.getPlayer());
            }
            event.setCancelled(true);
        }
    }
}
