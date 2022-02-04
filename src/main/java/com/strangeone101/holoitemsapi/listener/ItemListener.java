package com.strangeone101.holoitemsapi.listener;

import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitemsapi.CustomItemRegistry;
import com.strangeone101.holoitemsapi.HoloItemsAPI;
import com.strangeone101.holoitemsapi.Properties;
import com.strangeone101.holoitemsapi.abilities.FoodAbility;
import com.strangeone101.holoitemsapi.interfaces.BlockInteractable;
import com.strangeone101.holoitemsapi.interfaces.Edible;
import com.strangeone101.holoitemsapi.interfaces.EntityInteractable;
import com.strangeone101.holoitemsapi.interfaces.Interactable;
import com.strangeone101.holoitemsapi.interfaces.Placeable;
import com.strangeone101.holoitemsapi.interfaces.Repairable;
import com.strangeone101.holoitemsapi.interfaces.Swingable;
import com.strangeone101.holoitemsapi.itemevent.EventCache;
import com.strangeone101.holoitemsapi.recipe.NonConsumableChoice;
import com.strangeone101.holoitemsapi.recipe.RecipeBuilder;
import com.strangeone101.holoitemsapi.recipe.RecipeManager;
import com.strangeone101.holoitemsapi.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ItemListener implements Listener {

    public static final HashSet<Material> INTERACTABLES = new HashSet<>();

    public static final HashSet<EntityType> GREENLIT_ENTITY_INTERACTIONS = new HashSet<>();
    public static final HashMap<EntityType, BiFunction<Entity, Material, Boolean>> ENTITY_INTERACTABLES = new HashMap<>();

    public static boolean DEBUG_MODE = false;

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
        if (!EventCache.isCached(event.getPlayer())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    EventCache.fullCache(event.getPlayer());
                }
            }.runTaskLater(HoloItemsAPI.getPlugin(), 1L);
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        //Release cache in 10 seconds after logout
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!event.getPlayer().isOnline()) {
                    EventCache.release(event.getPlayer());
                }
            }
        }.runTaskLater(HoloItemsAPI.getPlugin(), 20 * 10L);
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

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY && (event.getAction() == Action.RIGHT_CLICK_BLOCK ||
                event.getAction() == Action.LEFT_CLICK_BLOCK)) return;
        if (event.useItemInHand() == Event.Result.DENY && (event.getAction() == Action.RIGHT_CLICK_AIR ||
                event.getAction() == Action.LEFT_CLICK_AIR)) return;

        CustomItem customItem = CustomItemRegistry.getCustomItem(event.getItem());
        int slot = event.getHand() == EquipmentSlot.OFF_HAND ? 40 : event.getPlayer().getInventory().getHeldItemSlot();

        boolean openInterface = event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null
                && INTERACTABLES.contains(event.getClickedBlock().getType()) && !event.getPlayer().isSneaking();

        //HoloItemsPlugin.INSTANCE.getLogger().info("Test----");
        if (customItem != null) {

            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            ItemStack item = event.getItem();

            //Run BlockInteractables before GUI checks
            if (customItem instanceof BlockInteractable && block != null) {
                //Use the item on the block
                if (((BlockInteractable)customItem).onInteract(player, block, customItem,
                        item, event.getAction() == Action.LEFT_CLICK_BLOCK)) {
                    event.setUseInteractedBlock(Event.Result.DENY);
                }
                if (item == null || item.getType() == Material.AIR) //Delete the item forcefully if it's set to air
                    player.getInventory().setItem(event.getHand(), null);
                return;
            }

            if (openInterface) {
                return; //Don't cancel the event because they opened some form of GUI
            }

            if (customItem instanceof Placeable) {
                return; //Don't cancel placeable items since they are handled in the place event
            }

            if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
                if (customItem instanceof Interactable) {
                    if (((Interactable)customItem).onInteract(player, customItem, item)) {
                        event.setUseItemInHand(Event.Result.DENY);
                    }
                    if (item == null || item.getType() == Material.AIR)
                        player.getInventory().setItem(event.getHand(), null);
                    return;
                }

                if (customItem instanceof Edible) {
                    event.setUseItemInHand(Event.Result.DENY);
                    new FoodAbility(player, item, player.getInventory(),
                            event.getHand() == EquipmentSlot.OFF_HAND ? 40 : player.getInventory().getHeldItemSlot());
                    return;
                }

                EquipmentSlot equipmentSlot = ItemUtils.getSlotForItem(customItem.getMaterial());
                if (equipmentSlot == EquipmentSlot.HEAD || equipmentSlot == EquipmentSlot.CHEST ||
                        equipmentSlot == EquipmentSlot.LEGS || equipmentSlot == EquipmentSlot.FEET) {
                    return; //Don't cancel since they are just trying to equip the armor
                }


            } else if (customItem instanceof Swingable && (event.getAction() == Action.LEFT_CLICK_AIR
                    || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
                ((Swingable)customItem).swing(player, customItem, item);
                if (item == null || item.getType() == Material.AIR)
                    player.getInventory().setItem(event.getHand(), null);
                return;
            }

            event.setUseItemInHand(Event.Result.DENY);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        CustomItem customItem = CustomItemRegistry.getCustomItem(event.getPlayer().getInventory().getItem(event.getHand()));

        if (customItem != null) {

            Player player = event.getPlayer();
            ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());

            if (customItem instanceof EntityInteractable) {
                if (((EntityInteractable)customItem).onInteract(event.getRightClicked(), event.getPlayer(), customItem, item)) {
                    event.setCancelled(true);
                }
                if (item == null || item.getType() == Material.AIR)
                    player.getInventory().setItem(event.getHand(), null);
                return;
            }

            EntityType type = event.getRightClicked().getType();
            Material mat = event.getPlayer().getInventory().getItem(event.getHand()).getType();

            //If it's not a greenlit entity and its not an entity that can be interacted with based on the item
            if (!(GREENLIT_ENTITY_INTERACTIONS.contains(type) ||
                    (ENTITY_INTERACTABLES.containsKey(type) && ENTITY_INTERACTABLES.get(type).apply(event.getRightClicked(), mat)))) {
                event.setCancelled(true);
            }
            if (item == null || item.getType() == Material.AIR)
                player.getInventory().setItem(event.getHand(), null);
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
        EventCache.updateHeldSlot(event.getPlayer(), event.getPreviousSlot(), event.getNewSlot());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onOffhandHotkey(PlayerSwapHandItemsEvent event) {
        // Swap cache slots between offhand and mainhand
        EventCache.swapCacheSlots(event.getPlayer(), 40, event.getPlayer().getInventory().getHeldItemSlot());
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
    public void onCraft(CraftItemEvent event) {
        ItemStack stack = event.getCurrentItem();
        if (CustomItemRegistry.isCustomItem(event.getCurrentItem()) && event.getWhoClicked() instanceof Player) {
            //Makes the output a fresh build of the item. Means it will be owned by that player
            event.setCurrentItem(CustomItemRegistry.getCustomItem(stack).buildStack((Player) event.getWhoClicked()));
        }

        if (!RecipeManager.isManagedRecipe(event.getRecipe())) {
            for (ItemStack ingredient : event.getInventory().getMatrix()) {
                if (CustomItemRegistry.isCustomItem(ingredient)) {
                    event.setCancelled(true);
                }
            }
        } else if (RecipeManager.isHiddenRecipe(event.getRecipe())) {
            RecipeBuilder.AdvancedRecipe recipe = RecipeManager.getAdvancedFromDummy(event.getRecipe());
            boolean notMatch = false;

            if (recipe instanceof RecipeBuilder.AdvancedShape) {
                RecipeBuilder.AdvancedShape advancedShape = (RecipeBuilder.AdvancedShape) recipe;
                CraftingInventory craftingInventory = event.getInventory();
                int size = craftingInventory.getSize() == 9 ? 3 : 2;
                int offset = 0;
                outter:
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        int currIndex = row * size + col;
                        if (craftingInventory.getMatrix()[currIndex] != null) {
                            offset = currIndex - advancedShape.getFirstNotEmpty();
                            break outter;
                        }
                    }
                }

                String[] shape = advancedShape.getShape();
                ItemStack[] matrix = craftingInventory.getMatrix();
                outter:
                for (int row = 0; row < shape.length; row++) {
                    for (int col = 0; col < shape[row].length(); col++) {
                        int matrixNum = offset + (row * 3) + col;
                        if (shape[row].charAt(col) == ' ') {
                            if (matrix[matrixNum] != null) {
                                notMatch = true;
                                break outter;
                            }
                        } else {
                            ItemStack craftingStack = matrix[matrixNum];
                            if (!advancedShape.checkStack(shape[row].charAt(col), craftingStack)) {
                                notMatch = true;
                                break outter;
                            }
                        }
                    }
                }
            }

            if (!notMatch) {
                ItemStack updated = recipe.getCraftModifier().create(event.getInventory().getResult(),
                        recipe.getInputItems(event.getInventory()), recipe.buildContext(event.getInventory(), event.getClick()));

                event.getInventory().setResult(updated);
            } else {
                event.setCancelled(true);
            }
        } else if (RecipeManager.isAdvancedRecipe(event.getRecipe())) {
            RecipeBuilder.AdvancedRecipe advRecipe = RecipeManager.getAdvancedRecipe(event.getRecipe());

            ItemStack updated = advRecipe.getCraftModifier().create(event.getInventory().getResult(),
                    advRecipe.getInputItems(event.getInventory()), advRecipe.buildContext(event.getInventory(), event.getClick()));

            event.getInventory().setResult(updated);
            event.setCurrentItem(updated);
        }

        if (RecipeManager.hasNonConsumable(event.getRecipe())) {
            Map<Integer, ItemStack> slots = new HashMap<>();
            for (int slot = 0; slot < event.getInventory().getSize(); slot++) {
                ItemStack slotItem = event.getInventory().getItem(slot);

                if (event.getRecipe() instanceof ShapedRecipe) {
                    for (RecipeChoice choice : ((ShapedRecipe) event.getRecipe()).getChoiceMap().values()) {
                        if (choice instanceof NonConsumableChoice && choice.test(slotItem)) {
                            slots.put(slot, slotItem.clone());
                            if (event.isShiftClick()) { //If they shift click,
                                slotItem.setAmount(64); //Allow it to craft as many as possible
                                event.getInventory().setItem(slot, slotItem);
                            }
                        }
                    }
                } else if (event.getRecipe() instanceof ShapelessRecipe) {
                    for (RecipeChoice choice : ((ShapelessRecipe) event.getRecipe()).getChoiceList()) {
                        if (choice instanceof NonConsumableChoice && choice.test(slotItem)) {
                            slots.put(slot, slotItem.clone());
                            if (event.isShiftClick()) { //If they shift click,
                                slotItem.setAmount(64); //Allow it to craft as many as possible
                                event.getInventory().setItem(slot, slotItem);
                            }
                        }
                    }
                }
            }

            //1 tick later, restore the items that were removed
            if (slots.size() > 0) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (int slot : slots.keySet()) {
                            event.getInventory().setItem(slot, slots.get(slot));
                        }
                    }
                }.runTaskLater(HoloItemsAPI.getPlugin(), 1L);
            }
        }
    }

    @EventHandler
    public void onPreCraft(PrepareItemCraftEvent event) {
        if (!RecipeManager.isManagedRecipe(event.getRecipe())) {
            for (ItemStack ingredient : event.getInventory().getMatrix()) {
                if (CustomItemRegistry.isCustomItem(ingredient)) {
                    event.getInventory().setResult(null); //Stops recipes using our custom items
                }
            }
        } else if (RecipeManager.isHiddenRecipe(event.getRecipe())) {
            RecipeBuilder.AdvancedRecipe recipe = RecipeManager.getAdvancedFromDummy(event.getRecipe());
            boolean notMatch = false;

            if (recipe instanceof RecipeBuilder.AdvancedShape) {
                RecipeBuilder.AdvancedShape advancedShape = (RecipeBuilder.AdvancedShape) recipe;
                CraftingInventory craftingInventory = event.getInventory();
                int size = craftingInventory.getSize() == 9 ? 3 : 2;
                int offset = 0;
                outter:
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        int currIndex = row * size + col;
                        if (craftingInventory.getMatrix()[currIndex] != null) {
                            offset = currIndex - advancedShape.getFirstNotEmpty();
                            break outter;
                        }
                    }
                }

                String[] shape = advancedShape.getShape();
                ItemStack[] matrix = craftingInventory.getMatrix();
                outter:
                for (int row = 0; row < shape.length; row++) {
                    for (int col = 0; col < shape[row].length(); col++) {
                        int matrixNum = offset + (row * 3) + col;
                        if (shape[row].charAt(col) == ' ') {
                            if (matrix[matrixNum] != null) {
                                notMatch = true;
                                break outter;
                            }
                        } else {
                            ItemStack craftingStack = matrix[matrixNum];
                            if (!advancedShape.checkStack(shape[row].charAt(col), craftingStack)) {
                                notMatch = true;
                                break outter;
                            }
                        }
                    }
                }
            }

            if (!notMatch) {
                ItemStack updated = recipe.getPreviewModifier().create(event.getInventory().getResult(),
                        recipe.getInputItems(event.getInventory()), recipe.buildContext(event.getInventory(), null));

                event.getInventory().setResult(updated);
            } else {
                event.getInventory().setResult(null);
            }
        } else if (RecipeManager.isAdvancedRecipe(event.getRecipe())) {
            RecipeBuilder.AdvancedRecipe advRecipe = RecipeManager.getAdvancedRecipe(event.getRecipe());

            ItemStack updated = advRecipe.getPreviewModifier().create(event.getInventory().getResult(),
                    advRecipe.getInputItems(event.getInventory()), advRecipe.buildContext(event.getInventory(), null));

            event.getInventory().setResult(updated);
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
                CustomItem ci = CustomItemRegistry.getCustomItem(stack);

                if (!ci.getProperties().contains(Properties.RENAMABLE) && !stack.getItemMeta().getDisplayName()
                        .equals(event.getInventory().getItem(0).getItemMeta().getDisplayName())) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(ChatColor.RED + "This item cannot be renamed!");
                    return;
                }

                if (!stack.getItemMeta().getDisplayName()
                        .equals(event.getInventory().getItem(0).getItemMeta().getDisplayName())) {
                    ItemMeta meta = stack.getItemMeta();
                    Properties.RENAMABLE.set(meta.getPersistentDataContainer(), 1); //Mark it as renamed
                    stack.setItemMeta(meta);
                    event.getClickedInventory().setItem(2, stack); //Set the updated item back into the inventory
                    event.setCurrentItem(stack);
                }

                if (ci instanceof Repairable) {
                    CustomItem ci2 = CustomItemRegistry.getCustomItem(event.getInventory().getItem(1));
                    if (((Repairable)ci).getRepairMaterial(stack) != ci2) {
                        event.setCancelled(true);
                        return;
                    }

                    int durability = (int) Math.min(ci.getMaxDurability(), ci.getDurability(stack)
                            + (((Repairable)ci).getRepairAmount(stack) * ci.getMaxDurability()));
                    ci.setDurability(stack, durability);
                    event.getClickedInventory().setItem(2, stack); //Set the updated item back into the inventory
                    event.setCurrentItem(stack);
                }

            }

        }

        if (DEBUG_MODE)
        event.getWhoClicked().sendMessage(event.getAction() + "," + event.getClick() + ","
                + event.getSlotType() + "," + event.getRawSlot() + "," + event.getSlot() + ","
                + event.getInventory().getSize() + "," + event.getInventory().getType());

        if (!(event.getWhoClicked() instanceof Player) || event.getAction() == InventoryAction.NOTHING) return;

        Player player = (Player)(event.getWhoClicked());

        if (event.getInventory().getType() == InventoryType.CREATIVE) {
            if (DEBUG_MODE) event.getWhoClicked().sendMessage("Dirty flag on: creative inventory");
            EventCache.DIRTY_INVENTORY.add(player);
            return;
        }

        if (EventCache.shouldCache(event.getCurrentItem()) || EventCache.shouldCache(event.getCursor())) {
            if (DEBUG_MODE) event.getWhoClicked().sendMessage("Dirty flag on: " + event.getAction().toString());
            EventCache.DIRTY_INVENTORY.add(player);
            return;
        }

        if (event.getAction() == InventoryAction.HOTBAR_SWAP) {
            //Special case: swap when a CustomItem is in offhand and cursor points to a regular item
            if (EventCache.shouldCache(player.getInventory().getItemInOffHand())) {
                if (DEBUG_MODE) event.getWhoClicked().sendMessage("Dirty flag on: " + event.getAction().toString());
                EventCache.DIRTY_INVENTORY.add(player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();

        if (EventCache.DIRTY_INVENTORY.contains(player)) {
            cacheLater(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDrag(InventoryDragEvent event) {
        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) return;
        if (EventCache.shouldCache(event.getCursor()) || EventCache.shouldCache(event.getOldCursor())) {
            if (DEBUG_MODE) event.getWhoClicked().sendMessage("Dirty flag: InventoryDragEvent");
            EventCache.DIRTY_INVENTORY.add((Player)entity);
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

            if (CustomItemRegistry.isCustomItem(slot1)) {
                CustomItem ci1 = CustomItemRegistry.getCustomItem(slot1);
                CustomItem ci2 = CustomItemRegistry.getCustomItem(slot2);

                //If it is repairable and the material is compatible, allow it
                if (ci1 instanceof Repairable) return ((Repairable)ci1).getRepairMaterial(slot1) != ci2;
                //If it can be renamed and there is nothing in slot 2, allow it
                if (ci1.getProperties().contains(Properties.RENAMABLE) && slot2 == null) return false;

                return true; //Block all other exceptions

            } else if (CustomItemRegistry.isCustomItem(slot2)) {
                return true; //Prevent custom items from being used to repair
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
                if (shouldPrevent(inventory)) {
                    inventory.setItem(2, null);
                } else if (inventory instanceof AnvilInventory) {
                    ItemStack slot1 = inventory.getItem(0);
                    ItemStack slot2 = inventory.getItem(1);

                    if (CustomItemRegistry.isCustomItem(slot1)) {
                        CustomItem ci1 = CustomItemRegistry.getCustomItem(slot1);
                        CustomItem ci2 = CustomItemRegistry.getCustomItem(slot2);

                        ItemStack slot3 = inventory.getItem(2);
                        if (slot3 == null) slot3 = slot1.clone();

                        //Prevent if it's not repairable OR the material doesn't match OR if it can't be renamed
                        if (ci1 instanceof Repairable) {
                            if (((Repairable)ci1).getRepairMaterial(slot1) != ci2) {
                                int durability = (int) Math.min(ci1.getMaxDurability(), ci1.getDurability(slot3)
                                        + (((Repairable)ci1).getRepairAmount(slot3) * ci1.getMaxDurability()));
                                ci1.setDurability(slot3, durability);
                            }
                        }
                    }
                }

            }
        }.runTaskLater(HoloItemsAPI.getPlugin(), 1L);
    }

    public void cacheLater(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                EventCache.fullCache(player);
            }
        }.runTaskLater(HoloItemsAPI.getPlugin(), 1L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBrewRefuel(BrewingStandFuelEvent event) {
        if (CustomItemRegistry.isCustomItem(event.getFuel())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRenamePre(PrepareAnvilEvent event) {
        //TODO Update this for repairables
        if (event.getInventory().getItem(1) != null) {
            if (CustomItemRegistry.isCustomItem(event.getInventory().getItem(1))) {
                event.setResult(null);
            }
        }
        if (event.getInventory().getItem(0) != null) {
            CustomItem item = CustomItemRegistry.getCustomItem(event.getInventory().getItem(0));

            if (item != null) {
                //Cancel if it can't be renamed
                if (!item.getProperties().contains(Properties.RENAMABLE)) {
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

    @EventHandler
    public void onRecipeSend(PlayerRecipeDiscoverEvent event) {
        if (RecipeManager.isHiddenRecipe(event.getRecipe())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                Player player = ((Player) event.getDamager());
                CustomItem ci = CustomItemRegistry.getCustomItem(player.getInventory().getItemInMainHand());

                if (ci != null) {
                    if (ci instanceof Swingable) {
                        double damage = ((Swingable) ci).hit(event.getEntity(), player, ci, player.getInventory().getItemInMainHand(), event.getDamage());
                        if (damage > 0) event.setDamage(damage);
                        else event.setCancelled(true);
                    }
                }
            } else if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                Player player = ((Player) event.getDamager());
                CustomItem ci = CustomItemRegistry.getCustomItem(player.getInventory().getItemInMainHand());

                if (ci != null) {
                    if (ci instanceof Swingable) {
                        double damage = ((Swingable) ci).sweep(event.getEntity(), player, ci, player.getInventory().getItemInMainHand(), event.getDamage());
                        if (damage > 0) event.setDamage(damage);
                        else event.setCancelled(true);
                    }
                }
            }
        }
    }
}
