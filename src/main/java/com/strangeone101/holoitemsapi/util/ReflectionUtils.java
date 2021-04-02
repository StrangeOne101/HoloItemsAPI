package com.strangeone101.holoitemsapi.util;

import com.google.common.base.Predicate;
import com.mojang.authlib.GameProfile;
import com.strangeone101.holoitemsapi.HoloItemsAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

    public static final String nms = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().substring(23);
    public static final String craft = "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().substring(23);

    private static boolean setup = false;

    private static Field repairCostField;
    private static Field loreField;
    private static Field displayNameField;
    private static Field profileField;
    private static Method forceCacheMethod;

    private static void setup() {
        try {
            Class craftMetaClass = Class.forName(craft + ".inventory.CraftMetaItem");
            repairCostField = craftMetaClass.getDeclaredField("repairCost");
            if (!repairCostField.isAccessible()) repairCostField.setAccessible(true);
            loreField = craftMetaClass.getDeclaredField("lore");
            if (!loreField.isAccessible()) loreField.setAccessible(true);
            displayNameField = craftMetaClass.getDeclaredField("displayName");
            if (!displayNameField.isAccessible()) displayNameField.setAccessible(true);

            Class craftMetaSkull = Class.forName(craft + ".inventory.CraftMetaSkull");
            profileField = craftMetaSkull.getDeclaredField("profile");
            if (!profileField.isAccessible()) profileField.setAccessible(true);

            Class tileEntitySkullClass = Class.forName(nms + ".TileEntitySkull");
            forceCacheMethod = tileEntitySkullClass.getDeclaredMethod("b", GameProfile.class, Predicate.class, Boolean.TYPE);
            if (!forceCacheMethod.isAccessible()) forceCacheMethod.setAccessible(true);

            setup = true;
        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static int getRepairCost(ItemStack stack) {
        if (!setup) setup();

        try {
            return (int) repairCostField.get(stack.getItemMeta());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<String> getTrueLore(ItemStack stack) {
        if (!setup) setup();

        ItemMeta meta = stack.getItemMeta();

        try {
            Object object = loreField.get(meta);
            if (object == null) return new ArrayList<>();
            return (List<String>) object;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setTrueLore(ItemStack item, BaseComponent... components) {
        if (!setup) setup();
        List<String> trueLore = new ArrayList<>(components.length);
        for (BaseComponent component : components) {
            if (component.toLegacyText().length() == 0) {
                trueLore.add(ComponentSerializer.toString(new TextComponent(""))); //Don't append the white and
                continue;                                                          //non italic prefix
            }
            BaseComponent lineBase = new ComponentBuilder().append("").color(ChatColor.WHITE)
                    .italic(false).getCurrentComponent();
            lineBase.addExtra(component);
            trueLore.add(ComponentSerializer.toString(lineBase));
        }
        try {
            ItemMeta meta = item.getItemMeta();
            loreField.set(meta, trueLore);
            item.setItemMeta(meta);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setTrueLore(ItemStack item, List<String> jsonList) {
        if (!setup) setup();
        try {
            ItemMeta meta = item.getItemMeta();
            loreField.set(meta, jsonList);

            item.setItemMeta(meta);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static String getTrueDisplayName(ItemStack stack) {
        if (!setup) setup();

        ItemMeta meta = stack.getItemMeta();

        try {
            Object object = displayNameField.get(meta);
            if (object == null) return null;
            return (String) object;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setTrueDisplayName(ItemStack stack, BaseComponent component) {
        if (!setup) setup();

        ItemMeta meta = stack.getItemMeta();

        try {
            displayNameField.set(meta, ComponentSerializer.toString(component));
            stack.setItemMeta(meta);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void forceCacheSkull(ItemStack stack) {
        if (stack.getType() != Material.PLAYER_HEAD) return;
        if (!setup) setup();

        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        GameProfile profile = new GameProfile(meta.getOwningPlayer().getUniqueId(), meta.getOwningPlayer().getName());
        Predicate<GameProfile> callback = (updatedProfile) -> {
            try {
                profileField.set(meta, updatedProfile);
                return true;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        };
        try {
            forceCacheMethod.invoke(null, profile, callback, false);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
