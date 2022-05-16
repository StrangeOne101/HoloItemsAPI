package com.strangeone101.holoitemsapi.loot;

import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * A registry class to register custom loot within the plugin
 */
public class CustomLootRegistry implements Listener {

    private static Map<EntityType, Set<LootTable>> ENTITY_TABLES = new HashMap<>();
    private static Map<Material, Set<BlockLootTable>> BLOCK_TABLES = new HashMap<>();
    private static Map<LootTable, Set<LootTableExtension>> EXTENSION_TABLES = new HashMap<>();

    /**
     * Register a loot table that triggers when an entity is killed
     * @param type The type of entity killed
     * @param table The custom loot table
     */
    public static void registerDeathTable(EntityType type, LootTable table) {
        if (!ENTITY_TABLES.containsKey(type)) {
            ENTITY_TABLES.put(type, new HashSet<>());
        }

        ENTITY_TABLES.get(type).add(table);
    }

    /**
     * Register a custom loot table that triggers when a block is broken
     * @param material The material to listen for
     * @param table The custom block loot table
     */
    public static void registerBlockBreakTable(Material material, BlockLootTable table) {
        if (!BLOCK_TABLES.containsKey(material)) {
            BLOCK_TABLES.put(material, new HashSet<>());
        }

        BLOCK_TABLES.get(material).add(table);
    }

    /**
     * Register a custom loot table that triggers when a block is broken
     * @param table The loot table to edit
     * @param extension The custom extension for this table
     */
    public static void registerLootExtension(LootTable table, LootTableExtension extension) {
        if (!EXTENSION_TABLES.containsKey(table)) {
            EXTENSION_TABLES.put(table, new HashSet<>());
        }

        EXTENSION_TABLES.get(table).add(extension);
    }

    /**
     * Clear all death tables out
     */
    public static void clearDeathTables() {
        ENTITY_TABLES.clear();
    }

    /**
     * Clear all block break loot tables out
     */
    public static void clearBlockBreakTables() {
        BLOCK_TABLES.clear();
    }

    /**
     * Clear all loot table extensions out
     */
    public static void clearLootExtensions() {
        EXTENSION_TABLES.clear();
    }

    /**
     * Called when an entity is killed.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        List<ItemStack> drops = event.getDrops();

        if (entity.getWorld().getGameRuleValue(GameRule.DO_MOB_LOOT) && ENTITY_TABLES.containsKey(entity.getType())) {

            if (entity.getKiller() == null) return;

            Location location = entity.getLocation();
            int looting_mod = entity.getKiller().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
            double luck_mod = entity.getKiller().getAttribute(Attribute.GENERIC_LUCK).getValue();

            LootContext.Builder builder = new LootContext.Builder(location);
            builder.lootedEntity(entity);
            builder.lootingModifier(looting_mod);
            builder.luck((float) luck_mod);
            builder.killer(entity.getKiller());
            LootContext lootContext = builder.build();

            for (LootTable table : ENTITY_TABLES.get(entity.getType())) {
                Collection<ItemStack> items = table.populateLoot(new Random(), lootContext);
                if (!items.isEmpty()) {
                    drops.addAll(items);
                }
            }
        }
    }

    /**
     * Called when a block is broken. <strong>Already handled by HoloItemsAPI - Do not call</strong>
     * @param event The block break event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer() != null && event.getBlock().getWorld().getGameRuleValue(GameRule.DO_TILE_DROPS)
                && event.getPlayer().getGameMode() != GameMode.CREATIVE && BLOCK_TABLES.containsKey(event.getBlock().getType())) {
            int fortune_mod = event.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            double luck_mod = event.getPlayer().getAttribute(Attribute.GENERIC_LUCK).getValue();
            boolean silk_mod = event.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0;

            BlockLootContext context = new BlockLootContext(event.getBlock());
            context.setPlayer(event.getPlayer());
            context.setFortune(fortune_mod);
            context.setLuck(luck_mod);
            context.setSilkTouch(silk_mod);

            Collection<ItemStack> drops = new ArrayList<>();
            for (BlockLootTable table : BLOCK_TABLES.get(event.getBlock().getType())) {
                List<ItemStack> tableDrops = new ArrayList<>();
                boolean b = table.populateLoot(tableDrops, new Random(), context);

                if (!b) {
                    event.setDropItems(false);
                }

                if (!tableDrops.isEmpty()) {
                    drops.addAll(tableDrops);
                }
            }

            if (!drops.isEmpty()) {
                for (ItemStack stack : drops) {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().subtract(0.5, 0.5, 0.5), stack);
                }
            }
        }
    }

    @EventHandler
    public void handleLootGenerating(LootGenerateEvent event) {
        if (EXTENSION_TABLES.containsKey(event.getLootTable()) && event.getEntity() instanceof Player) {
            List<ItemStack> stacks = event.getLoot();

            for (LootTableExtension extension : EXTENSION_TABLES.get(event.getLootTable())) {
                extension.populateLoot(event.getLootTable(), stacks, new Random(), (Player) event.getEntity());
            }
        }
    }
}
