package com.strangeone101.holoitemsapi.util;

import com.strangeone101.holoitemsapi.HoloItemsAPI;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public class ItemStackContainer implements PersistentDataType<byte[], ItemStack> {

    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<ItemStack> getComplexType() {
        return ItemStack.class;
    }

    @Override
    public byte[] toPrimitive(ItemStack complex, PersistentDataAdapterContext context) {
        return HoloItemsAPI.getNMS().writeNBTBytes(complex);
    }

    @Override
    public ItemStack fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
        return HoloItemsAPI.getNMS().readNBTBytes(primitive);
    }
}
