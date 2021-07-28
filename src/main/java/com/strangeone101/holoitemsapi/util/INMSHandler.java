package com.strangeone101.holoitemsapi.util;

import com.mojang.authlib.GameProfile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface INMSHandler {

    String getVersion();

    byte[] writeNBTBytes(ItemStack stack);

    ItemStack readNBTBytes(byte[] data);

    void updateSkullCache(GameProfile profile, Consumer<GameProfile> consumer);

    void sendFakeItem(Player player, int slot, ItemStack stack);

    <T> T readNBT(Class<T> clazzType, String key, ItemStack stack);

    <T> void writeNBT(Class<T> clazzType, T value, String key, ItemStack stack);

    boolean containsNBT(String key, ItemStack stack);
}
