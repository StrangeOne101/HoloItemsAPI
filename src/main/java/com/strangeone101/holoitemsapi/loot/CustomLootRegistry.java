package com.strangeone101.holoitemsapi.loot;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.BlockBreakEvent;
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
public class CustomLootRegistry {

    private static Map<EntityType, Set<LootTable>> ENTITY_TABLES = new HashMap<>();
    private static Map<Material, Set<BlockLootTable>> BLOCK_TABLES = new HashMap<>();

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
     * Called when an entity is killed. <strong>Already handled by HoloItemsAPI - Do not call</strong>
     * @param entity The entity killed
     * @param drops The list of drops they dropped
     */
    public static void handleDeath(LivingEntity entity, List<ItemStack> drops) {
        if (ENTITY_TABLES.containsKey(entity.getType())) {

            //HoloItemsPlugin.INSTANCE.getLogger().info("Debug here 444");

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

            //HoloItemsPlugin.INSTANCE.getLogger().info("Debug here 555");

            for (LootTable table : ENTITY_TABLES.get(entity.getType())) {
                Collection<ItemStack> items = table.populateLoot(new Random(), lootContext);
                if (!items.isEmpty()) {
                    drops.addAll(items);
                    //HoloItemsPlugin.INSTANCE.getLogger().info("Debug here 666");
                }
            }
        }
    }

    /**
     * Called when a block is broken. <strong>Already handled by HoloItemsAPI - Do not call</strong>
     * @param event The block break event
     */
    public static void handleBlockBreak(BlockBreakEvent event) {
        if (BLOCK_TABLES.containsKey(event.getBlock().getType())) {
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
}
