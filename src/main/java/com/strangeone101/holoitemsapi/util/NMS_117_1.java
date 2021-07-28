package com.strangeone101.holoitemsapi.util;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Ref;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class NMS_117_1 implements INMSHandler {

    public NMS_117_1() {
        ReflectionUtils.setup();
    }

    @Override
    public String getVersion() {
        return "1.17";
    }

    public byte[] writeNBTBytes(ItemStack stack) {
        try {
            net.minecraft.world.item.ItemStack nmsCopy = (net.minecraft.world.item.ItemStack) ReflectionUtils.asNMSCopy.invoke(null, stack);
            CompoundTag newTag = nmsCopy.save(new CompoundTag());

            ByteArrayDataOutput byteArray = ByteStreams.newDataOutput();
            newTag.write(byteArray);

            return byteArray.toByteArray();
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    public ItemStack readNBTBytes(byte[] bytes) {
        ByteArrayDataInput byteInput = ByteStreams.newDataInput(bytes);
        try {
            CompoundTag tag = NbtIo.read(byteInput);
            net.minecraft.world.item.ItemStack itemstack = net.minecraft.world.item.ItemStack.of(tag);
            return CraftItemStack.asCraftMirror(itemstack);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateSkullCache(GameProfile profile, Consumer<GameProfile> consumer) {
        SkullBlockEntity.updateGameprofile(profile, consumer);
    }

    @Override
    public void sendFakeItem(Player player, int slot, ItemStack stack) {
        try {
            net.minecraft.world.item.ItemStack nmsCopy = (net.minecraft.world.item.ItemStack) ReflectionUtils.asNMSCopy.invoke(null, stack);
            ServerPlayer handlePlayer = (ServerPlayer) ReflectionUtils.getHandle.invoke(player);
            ServerPlayerConnection connection = handlePlayer.connection;
            int fixedSlot = slot + 9;
            if (slot > 35) fixedSlot = Math.abs((slot - 35) - 5);
            else if (slot >= 0 && slot <= 8) fixedSlot = slot + 27 + 9;
            ClientboundContainerSetSlotPacket packet = new ClientboundContainerSetSlotPacket(0, 0, fixedSlot, (net.minecraft.world.item.ItemStack) nmsCopy);
            connection.send(packet);

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> T readNBT(Class<T> clazzType, String key, ItemStack stack) {
        try {

            net.minecraft.world.item.ItemStack nmsCopy = (net.minecraft.world.item.ItemStack) ReflectionUtils.asNMSCopy.invoke(null, stack);
            CompoundTag tag = nmsCopy.getOrCreateTag();

            while (key.contains(".")) {
                String thisKey = key.split(".")[0];
                key = key.split(".")[1];

                tag = tag.getCompound(thisKey);
            }

            if (boolean.class.equals(clazzType)) {
                return clazzType.cast(tag.getBoolean(key));
            } else if (int.class.equals(clazzType)) {
                return clazzType.cast(tag.getInt(key));
            } else if (byte.class.equals(clazzType)) {
                return clazzType.cast(tag.getByte(key));
            } else if (short.class.equals(clazzType)) {
                return clazzType.cast(tag.getShort(key));
            } else if (long.class.equals(clazzType)) {
                return clazzType.cast(tag.getLong(key));
            } else if (float.class.equals(clazzType)) {
                return clazzType.cast(tag.getFloat(key));
            } else if (double.class.equals(clazzType)) {
                return clazzType.cast(tag.getDouble(key));
            } else if (String.class.equals(clazzType)) {
                return clazzType.cast(tag.getString(key));
            } else if (int[].class.equals(clazzType)) {
                return clazzType.cast(tag.getIntArray(key));
            } else if (byte[].class.equals(clazzType)) {
                return clazzType.cast(tag.getByteArray(key));
            } else if (long[].class.equals(clazzType)) {
                return clazzType.cast(tag.getLongArray(key));
            } else if (UUID.class.equals(clazzType)) {
                return clazzType.cast(tag.getUUID(key));
            } else if (List.class.equals(clazzType)) {
                throw new UnsupportedOperationException("Lists not supported yet");
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> ItemStack writeNBT(Class<T> clazzType, T value, String key, ItemStack stack) {
        try {

            net.minecraft.world.item.ItemStack nmsCopy = (net.minecraft.world.item.ItemStack) ReflectionUtils.asNMSCopy.invoke(null, stack);
            CompoundTag tag = nmsCopy.getOrCreateTag();

            while (key.contains(".")) {
                String thisKey = key.split(".")[0];
                key = key.split(".")[1];

                tag = tag.getCompound(thisKey);
            }

            if (boolean.class.equals(clazzType)) {
                tag.putBoolean(key, (boolean)value);
            } else if (int.class.equals(clazzType)) {
                tag.putInt(key, (int)value);
            } else if (byte.class.equals(clazzType)) {
                tag.putByte(key, (byte)value);
            } else if (short.class.equals(clazzType)) {
                tag.putShort(key, (short)value);
            } else if (long.class.equals(clazzType)) {
                tag.putLong(key, (long)value);
            } else if (float.class.equals(clazzType)) {
                tag.putFloat(key, (float)value);
            } else if (double.class.equals(clazzType)) {
                tag.putDouble(key, (double)value);
            } else if (String.class.equals(clazzType)) {
                tag.putString(key, (String)value);
            } else if (int[].class.equals(clazzType)) {
                tag.putIntArray(key, (int[])value);
            } else if (byte[].class.equals(clazzType)) {
                tag.putByteArray(key, (byte[])value);
            } else if (long[].class.equals(clazzType)) {
                tag.putLongArray(key, (long[])value);
            } else if (UUID.class.equals(clazzType)) {
                tag.putUUID(key, (UUID)value);
            } else if (List.class.equals(clazzType)) {
                throw new UnsupportedOperationException("Lists not supported yet");
            }
            nmsCopy.setTag(tag);
            return (stack = CraftItemStack.asBukkitCopy(nmsCopy));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stack;
    }

    @Override
    public boolean containsNBT(String key, ItemStack stack) {
        try {

            net.minecraft.world.item.ItemStack nmsCopy = (net.minecraft.world.item.ItemStack) ReflectionUtils.asNMSCopy.invoke(null, stack);
            CompoundTag tag = nmsCopy.getOrCreateTag();

            while (key.contains(".")) {
                String thisKey = key.split(".")[0];
                key = key.split(".")[1];

                tag = tag.getCompound(thisKey);
            }

            return tag.contains(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
