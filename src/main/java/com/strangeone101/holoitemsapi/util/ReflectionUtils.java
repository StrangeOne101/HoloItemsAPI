package com.strangeone101.holoitemsapi.util;

import com.google.common.base.Predicate;
import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {

    public static final String nms = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().substring(23);
    public static final String craft = "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().substring(23);

    private static boolean setup = false;

    private static Field repairCostField;
    private static Field profileField;
    private static Method forceCacheMethod;

    private static void setup() {
        try {
            Class craftStackClass = Class.forName(craft + ".inventory.CraftMetaItem");
            repairCostField = craftStackClass.getDeclaredField("repairCost");
            if (!repairCostField.isAccessible()) repairCostField.setAccessible(true);

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
