package com.strangeone101.holoitems.loot;

import com.strangeone101.holoitems.HoloItemsPlugin;
import com.strangeone101.holoitems.loot.tables.Endermite;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class CustomLootRegistry {

    private static Map<EntityType, Set<LootTable>> ENTITY_TABLES = new HashMap<>();

    public static void registerDeathTable(EntityType type, LootTable table) {
        if (!ENTITY_TABLES.containsKey(type)) {
            ENTITY_TABLES.put(type, new HashSet<>());
        }

        ENTITY_TABLES.get(type).add(table);
    }

    public static void handleDeath(LivingEntity entity, List<ItemStack> drops) {
        if (ENTITY_TABLES.containsKey(entity.getType())) {

            HoloItemsPlugin.INSTANCE.getLogger().info("Debug here 444");

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

            HoloItemsPlugin.INSTANCE.getLogger().info("Debug here 555");

            for (LootTable table : ENTITY_TABLES.get(entity.getType())) {
                Collection<ItemStack> items = table.populateLoot(new Random(), lootContext);
                if (!items.isEmpty()) {
                    drops.addAll(items);
                    HoloItemsPlugin.INSTANCE.getLogger().info("Debug here 666");
                }
            }
        }
    }

    static {
        registerDeathTable(EntityType.ENDERMITE, new Endermite());
    }
}
