package com.strangeone101.holoitemsapi.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.strangeone101.holoitemsapi.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class ItemUtils {

    public static final Map<CustomItem, Map<Attribute, UUID>> ATTRIBUTE_MAP = new HashMap<>();

    /**
     * Sets the skin of a Skull to the skin provided. Can be a UUID, name, texture ID or URL
     * @param meta The skull meta
     * @param skin The skin
     */
    public static void setSkin(SkullMeta meta, String skin) {

        try {
            UUID uuid = UUID.fromString(skin);
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
            return;
        } catch (IllegalArgumentException ignored) { }

        if (skin.startsWith("https://")) {
            setSkinFromURL(meta, skin);
        } else if (skin.matches("[a-f\\d]{64}")) {
            setSkinFromURL(meta, "http://textures.minecraft.net/texture/" + skin);
        } else {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(skin));
        }
    }

    /**
     * Sets the skin of a Skull to the skin from the provided URL
     * @param meta The skull meta
     * @param skin The skin URL
     * @return The corrected ItemMeta
     */
    public static SkullMeta setSkinFromURL(SkullMeta meta, String skin) {
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta im = (SkullMeta) stack.getItemMeta();
        UUID uuid;
        Random random = new Random(skin.hashCode());

        GameProfile profile = new GameProfile(new UUID(random.nextLong(), random.nextLong()), null);
        byte[] encodedData = Base64.getEncoder()
                .encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", skin).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        try {
            Field profileField = im.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(im, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        return im;
    }

    /**
     * Sets attributes on an item
     * @param number The amount to use
     * @param attribute The attribute
     * @param operation The operation
     * @param stack The itemstack
     */
    public static void setAttribute(double number, Attribute attribute, AttributeModifier.Operation operation, ItemStack stack, CustomItem ci) {
        if (!(ATTRIBUTE_MAP.containsKey(ci))) {
            ATTRIBUTE_MAP.put(ci, new HashMap<>());
        }
        Map<Attribute, UUID> ciMap = ATTRIBUTE_MAP.get(ci);
        if (ATTRIBUTE_MAP.get(ci).isEmpty()) {
            Random random = new Random(ci.getInternalName().hashCode());

            //Due to the set random seed above, the UUIDs bellow should always be the same.
            ciMap.put(Attribute.GENERIC_ATTACK_DAMAGE, new UUID(random.nextLong(), random.nextLong()));
            ciMap.put(Attribute.GENERIC_ATTACK_SPEED, new UUID(random.nextLong(), random.nextLong()));
            ciMap.put(Attribute.GENERIC_ATTACK_KNOCKBACK, new UUID(random.nextLong(), random.nextLong()));
            ciMap.put(Attribute.GENERIC_MAX_HEALTH, new UUID(random.nextLong(), random.nextLong()));
            ciMap.put(Attribute.GENERIC_ARMOR, new UUID(random.nextLong(), random.nextLong()));
            ciMap.put(Attribute.GENERIC_ARMOR_TOUGHNESS, new UUID(random.nextLong(), random.nextLong()));
            ciMap.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new UUID(random.nextLong(), random.nextLong()));
            ciMap.put(Attribute.GENERIC_MOVEMENT_SPEED, new UUID(random.nextLong(), random.nextLong()));
            ciMap.put(Attribute.GENERIC_FLYING_SPEED, new UUID(random.nextLong(), random.nextLong()));
            ciMap.put(Attribute.GENERIC_FOLLOW_RANGE, new UUID(random.nextLong(), random.nextLong()));
            ciMap.put(Attribute.GENERIC_LUCK, new UUID(random.nextLong(), random.nextLong()));
            ciMap.put(Attribute.HORSE_JUMP_STRENGTH, new UUID(random.nextLong(), random.nextLong()));
            ciMap.put(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS, new UUID(random.nextLong(), random.nextLong()));

            if (!ciMap.containsKey(attribute)) { //Bodge for attributes added in future
                ciMap.put(attribute, new UUID(random.nextLong(), random.nextLong()));
            }
        }

        try {
            AttributeModifier mod = new AttributeModifier(ciMap.get(attribute), attribute.name(), number, operation, getSlotForItem(stack.getType()));
            ItemMeta meta = stack.getItemMeta();
            meta.addAttributeModifier(attribute, mod);
            stack.setItemMeta(meta);
        } catch (IllegalArgumentException e) {

        }
    }

    /**
     * Gets what Equipment slot this item should be used for
     * @param material The material
     * @return The EquipmentSlot
     */
    public static EquipmentSlot getSlotForItem(Material material) {
        switch (material) {
            case SHIELD:
                return EquipmentSlot.OFF_HAND;
            case LEATHER_BOOTS:
            case IRON_BOOTS:
            case GOLDEN_BOOTS:
            case CHAINMAIL_BOOTS:
            case DIAMOND_BOOTS:
            case NETHERITE_BOOTS:
                return EquipmentSlot.FEET;
            case LEATHER_LEGGINGS:
            case IRON_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case GOLDEN_LEGGINGS:
            case DIAMOND_LEGGINGS:
            case NETHERITE_LEGGINGS:
                return EquipmentSlot.LEGS;
            case LEATHER_CHESTPLATE:
            case IRON_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
            case NETHERITE_CHESTPLATE:
            case ELYTRA:
                return EquipmentSlot.CHEST;
            case LEATHER_HELMET:
            case IRON_HELMET:
            case CHAINMAIL_HELMET:
            case GOLDEN_HELMET:
            case DIAMOND_HELMET:
            case NETHERITE_HELMET:
            case TURTLE_HELMET:
            case CARVED_PUMPKIN:
                return EquipmentSlot.HEAD;
            default:
                return EquipmentSlot.HAND;
        }
    }

    /**
     * Is this material meat?
     * @param material The material?
     * @return True if it's meat
     */
    public static boolean isMeat(Material material) {
        switch (material) {
            case BEEF:          case COOKED_BEEF:
            case MUTTON:        case COOKED_MUTTON:
            case COD:           case COOKED_COD:
            case SALMON:        case COOKED_SALMON:
            case RABBIT:        case COOKED_RABBIT:
            case CHICKEN:       case COOKED_CHICKEN:
            case PORKCHOP:      case COOKED_PORKCHOP:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this material fish?
     * @param material The material?
     * @return True if it's fish
     */
    public static boolean isFish(Material material) {
        switch (material) {
            case COD:               case COOKED_COD:
            case SALMON:            case COOKED_SALMON:
            case TROPICAL_FISH:     case PUFFERFISH:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this material a dye?
     * @param material The material?
     * @return True if it's a dye
     */
    public static boolean isDye(Material material) {
        switch (material) {
            case WHITE_DYE:         case BLACK_DYE:
            case BLUE_DYE:          case BROWN_DYE:
            case CYAN_DYE:          case GRAY_DYE:
            case GREEN_DYE:         case LIGHT_BLUE_DYE:
            case LIGHT_GRAY_DYE:    case LIME_DYE:
            case MAGENTA_DYE:       case ORANGE_DYE:
            case PINK_DYE:          case PURPLE_DYE:
            case RED_DYE:           case YELLOW_DYE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this material leather armor?
     * @param material The material
     * @return True if it's leather
     */
    public static boolean isLeatherArmor(Material material) {
        switch (material) {
            case LEATHER_BOOTS:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_HELMET:
                return true;
            default:
                return false;
        }
    }
}
