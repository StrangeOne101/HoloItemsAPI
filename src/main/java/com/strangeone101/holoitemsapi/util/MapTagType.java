package com.strangeone101.holoitemsapi.util;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public class MapTagType implements PersistentDataType<byte[], Map> {

    public static MapTagType TYPE;

    static {
        TYPE = new MapTagType();
    }

    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<Map> getComplexType() {
        return Map.class;
    }

    @Override
    public byte[] toPrimitive(Map map, PersistentDataAdapterContext context) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(map);
            return byteOut.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    @Override
    public Map fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(primitive);
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(byteIn);
            return (Map) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
