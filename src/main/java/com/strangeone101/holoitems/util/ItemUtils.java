package com.strangeone101.holoitems.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

public class ItemUtils {

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

    public static void sendFakeItem(Player player, int slot, ItemStack stack) {
        String nms = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().substring(23);
        String craft = "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().substring(23);

        try {
            Class packetClass = Class.forName(nms + ".PacketPlayOutSetSlot");
            Class itemStack = Class.forName(nms + ".ItemStack");
            Class craftStackClass = Class.forName(craft + ".inventory.CraftItemStack");
            Class craftPlayerClass = Class.forName(craft + ".entity.CraftPlayer");
            Class abstractPacketClass = Class.forName(nms + ".Packet");
            Method asNMSCopy = craftStackClass.getDeclaredMethod("asNMSCopy", ItemStack.class);
            Method getHandle = craftPlayerClass.getDeclaredMethod("getHandle");
            Object nmsCopy = asNMSCopy.invoke(null, stack);
            Object handlePlayer = getHandle.invoke(player);
            Object connection = handlePlayer.getClass().getDeclaredField("playerConnection").get(handlePlayer);
            Method sendPacket = connection.getClass().getDeclaredMethod("sendPacket", abstractPacketClass);
            Constructor constructor = packetClass.getDeclaredConstructor(Integer.TYPE, Integer.TYPE, itemStack);
            int fixedSlot = slot + 9;
            if (slot > 35) fixedSlot = Math.abs((slot - 35) - 5);
            else if (slot >= 0 && slot <= 8) fixedSlot = slot + 27 + 9;
            Object packet = constructor.newInstance(0, fixedSlot, nmsCopy);
            sendPacket.invoke(connection, packet);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
