package com.strangeone101.holoitemsapi.util;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Colorable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EntityUtils {

    public static final int SKIP_HEALTH = 0x0001;
    public static final int SKIP_NAME = 0x0002;
    public static final int SKIP_AGE = 0x0004;
    public static final int SKIP_VARIANT = 0x0008;
    public static final int SKIP_LOCATION = 0x0010;
    public static final int SKIP_VELOCITY = 0x0020;
    public static final int SKIP_OWNER = 0x0040;
    public static final int SKIP_LIVING_EFFECTS = 0x0080;
    public static final int SKIP_INVENTORY = 0x0100;
    public static final int SKIP_POTIONS = 0x0200;
    public static final int SKIP_MEMORIES = 0x0400;
    public static final int SKIP_TRADES = 0x0800;
    public static final int SKIP_ATTRIBUTES = 0x1000;

    /**
     * Gets all the properties of an entity and puts them all in a map. Should only be used for LivingEntities
     * @param entity The entity to encode
     * @param flags The flags to skip on the entity
     * @return The properties
     */
    public static Map<String, Object> encode(Entity entity, int flags) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("type", entity.getType().toString());

        if (!test(flags, SKIP_NAME)) {
            String name = entity.getCustomName();
            if (name != null)
            properties.put("name", name);
            properties.put("nameVisible", entity.isCustomNameVisible());
        }

        //Age
        if (!test(flags, SKIP_AGE)) {
            properties.put("tickedLived", entity.getTicksLived());
            if (entity instanceof Ageable) {
                properties.put("age", ((Ageable) entity).getAge());
            }
        }

        //Location
        if (!test(flags, SKIP_LOCATION)) {
            properties.put("pos", entity.getLocation());
        }

        //Velocity
        if (!test(flags, SKIP_VELOCITY)) {
            properties.put("motion", entity.getVelocity());
        }

        //Attributes, Memories, Health, Potions and on fire, etc
        if (entity instanceof LivingEntity) {

            if (!test(flags, SKIP_ATTRIBUTES)) {
                List<AttributeModifier> modifiers = new ArrayList<>();
                for (Attribute attr : Attribute.values()) {
                    AttributeInstance a = ((LivingEntity) entity).getAttribute(attr);

                    if (a != null) modifiers.addAll(a.getModifiers());
                }
                properties.put("attributes", modifiers);
            }

            if (!test(flags, SKIP_HEALTH))
                properties.put("health", ((LivingEntity) entity).getHealth());

            if (!test(flags, SKIP_POTIONS))
                properties.put("potions", ((LivingEntity) entity).getActivePotionEffects());

            if (!test(flags, SKIP_LIVING_EFFECTS)) {
                properties.put("fire", entity.getFireTicks());
                properties.put("invincible", ((LivingEntity) entity).getNoDamageTicks());
            }

            properties.put("itempickup", ((LivingEntity) entity).getCanPickupItems());

            if (!test(flags, SKIP_MEMORIES)) {
                Map<String, Object> memories = new HashMap<>();

                for (MemoryKey key : MemoryKey.values()) {
                    Object o = ((LivingEntity) entity).getMemory(key);
                    if (o != null) memories.put(key.toString(), o);
                }
                properties.put("memories", memories);
            }
        }

        if (entity instanceof Tameable && !test(flags, SKIP_OWNER)) {
            properties.put("owner", ((Tameable) entity).getOwner().getUniqueId());
            properties.put("tamed", ((Tameable) entity).isTamed());
        }

        if (entity instanceof Cat && !test(flags, SKIP_VARIANT)) {
            properties.put("collar", ((Cat) entity).getCollarColor().ordinal()); //Convert Color to number
            properties.put("variant", ((Cat) entity).getCatType().ordinal());    //Convert type to number
        } else if (entity instanceof Wolf) {
            properties.put("collar", ((Wolf) entity).getCollarColor().ordinal());
        } else if (entity instanceof Horse && !test(flags, SKIP_VARIANT)) {
            int variant = ((Horse) entity).getColor().ordinal();
            variant += ((Horse) entity).getStyle().ordinal() << 8;

            properties.put("variant", variant);
            properties.put("temper", ((Horse) entity).getDomestication());
        } else if (entity instanceof TropicalFish && !test(flags, SKIP_VARIANT)) {
            int variant = ((TropicalFish) entity).getBodyColor().ordinal();
            variant += ((TropicalFish) entity).getPatternColor().ordinal() << 8;
            variant += ((TropicalFish) entity).getPattern().ordinal() << 16;
            properties.put("variant", variant);
        } else if (entity instanceof Parrot && !test(flags, SKIP_VARIANT)) {
            properties.put("variant", ((Parrot) entity).getVariant().ordinal());
        } else if (entity instanceof Colorable && !test(flags, SKIP_VARIANT)) { //Sheep and Shulker
            if (((Colorable) entity).getColor() != null)
                properties.put("variant", ((Colorable) entity).getColor().ordinal());
        } else if (entity instanceof Llama && !test(flags, SKIP_VARIANT)) {
            properties.put("variant", ((Llama) entity).getColor().ordinal());
            properties.put("strength", ((Llama) entity).getStrength());
        } else if (entity instanceof Rabbit && !test(flags, SKIP_VARIANT)) {
            properties.put("variant", ((Rabbit) entity).getRabbitType().ordinal());
        } else if (entity instanceof Creeper && !test(flags, SKIP_VARIANT)) {
            properties.put("variant", ((Creeper) entity).isPowered());
        } else if (entity instanceof Snowman) {
            properties.put("derp", ((Snowman) entity).isDerp());
        } else if (entity instanceof IronGolem) {
            properties.put("playermade", ((IronGolem) entity).isPlayerCreated());
        } else if (entity instanceof Villager) {
            if (!test(flags, SKIP_VARIANT)) {
                properties.put("variant", ((Villager) entity).getVillagerType().ordinal());
            }

            if (!test(flags, SKIP_TRADES)) {
                properties.put("trades", ((Villager) entity).getRecipes());
                properties.put("profession", ((Villager) entity).getProfession().ordinal());
                properties.put("exp", ((Villager) entity).getVillagerExperience());
                properties.put("lvl", ((Villager) entity).getVillagerLevel());
            }
        }

        if (entity instanceof InventoryHolder && !test(flags, SKIP_INVENTORY)) {
            properties.put("inventory", ((InventoryHolder) entity).getInventory());
        }

        return properties;
    }

    private static boolean test(int flags, int testFlag) {
        return (flags & testFlag) == testFlag;
    }

    /**
     * Gets all the properties in the map and applies them to the entity
     * @param entity The entity to apply the properties to
     * @param properties The properties
     */
    public static void decode(Entity entity, Map<String, Object> properties) {
        if (properties.containsKey("motion")) entity.setVelocity((Vector) properties.get("motion"));

        if (properties.containsKey("name")) entity.setCustomName((String) properties.get("name"));
        if (properties.containsKey("nameVisible")) entity.setCustomNameVisible((boolean) properties.get("nameVisible"));
        if (properties.containsKey("ticksLived")) entity.setTicksLived((int) properties.get("ticksLived"));

        if (entity instanceof Ageable) {
            ((Ageable) entity).setAge((int) properties.get("age"));
        }

        if (entity instanceof LivingEntity) {
            if (properties.containsKey("attributes")) {
                List<AttributeModifier> modifiers = (List<AttributeModifier>) properties.get("attributes");
                for (AttributeModifier mod : modifiers) {
                    Attribute attribute = Attribute.valueOf(mod.getName());
                    if (((LivingEntity) entity).getAttribute(attribute) != null)
                        ((LivingEntity) entity).getAttribute(attribute).addModifier(mod);
                }
            }

            if (properties.containsKey("health")) ((LivingEntity) entity).setHealth((Double) properties.get("health"));
            if (properties.containsKey("fire")) entity.setFireTicks((Integer) properties.get("fire"));
            if (properties.containsKey("invincible")) ((LivingEntity) entity).setNoDamageTicks((Integer) properties.get("invincible"));
            if (properties.containsKey("itempickup")) ((LivingEntity) entity).setCanPickupItems((boolean) properties.get("itempickup"));

            if (properties.containsKey("potions")) {
                Collection<PotionEffect> potions = (Collection<PotionEffect>) properties.get("potions");
                ((LivingEntity) entity).addPotionEffects(potions);
            }

        }

        if (entity instanceof Tameable) {
            if (properties.containsKey("tamed")) {
                ((Tameable) entity).setTamed((boolean) properties.get("tamed"));
            }
            if (properties.containsKey("owner")) {
                OfflinePlayer owner = Bukkit.getOfflinePlayer((UUID)properties.get("owner"));
                ((Tameable) entity).setOwner(owner);
            }
        }

        if (entity instanceof Cat) {
            if (properties.containsKey("collar")) ((Cat) entity).setCollarColor(DyeColor.values()[(int)properties.get("collar")]);
            if (properties.containsKey("variant")) ((Cat) entity).setCatType(Cat.Type.values()[(int)properties.get("variant")]);
        } else if (entity instanceof Wolf) {
            if (properties.containsKey("collar")) ((Wolf) entity).setCollarColor(DyeColor.values()[(int)properties.get("collar")]);
        } else if (entity instanceof Horse) {
            if (properties.containsKey("variant")) {
                int variant = (int) properties.get("variant");
                int color = variant & 0xFF;
                int style = variant >> 8;
                ((Horse) entity).setStyle(Horse.Style.values()[style]);
                ((Horse) entity).setColor(Horse.Color.values()[color]);
            }

            if (properties.containsKey("temper")) ((Horse) entity).setDomestication((int)properties.get("temper"));
        }
    }

    /**
     * Create and decode an entity from a property map. MUST have the location included in the map, or see {@link #decodeCreate(Map, Location)}
     * @param properties The properties
     * @return The decoded mob
     */
    public static Entity decodeCreate(Map<String, Object> properties) {
        if (!properties.containsKey("pos")) throw new UnsupportedOperationException("Cannot create an entity " +
                "when a location to spawn the entity has not been provided!");

        Location location = (Location) properties.get("pos");
        return decodeCreate(properties, location);
    }

    /**
     * Create and decode an entity from a property map and spawns it at the location
     * @param properties The property map
     * @param location The location to spawn at
     * @return The decoded and spawned mob
     */
    public static Entity decodeCreate(Map<String, Object> properties, Location location) {
        EntityType type = EntityType.valueOf((String) properties.get("type"));

        Entity entity = location.getWorld().spawnEntity(location, type);

        decode(entity, properties);

        //TODO FINISH THE REST OF THE MOBS

        return entity;
    }
}
