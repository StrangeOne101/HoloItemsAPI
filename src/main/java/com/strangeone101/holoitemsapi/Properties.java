package com.strangeone101.holoitemsapi;

import com.strangeone101.holoitems.HoloItemsPlugin;
import com.strangeone101.holoitemsapi.util.UUIDTagType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Properties {

    public static final Property<UUID> OWNER = new Property<UUID>() {

        @Override
        public boolean has(PersistentDataContainer data) {
            return data.has(HoloItemsAPI.getKeys().CUSTOM_ITEM_OWNER, UUIDTagType.TYPE);
        }

        @Override
        public UUID get(PersistentDataContainer data) {
            return data.get(HoloItemsAPI.getKeys().CUSTOM_ITEM_OWNER, UUIDTagType.TYPE);
        }

        @Override
        public void set(PersistentDataContainer data, UUID value) {
            data.set(HoloItemsAPI.getKeys().CUSTOM_ITEM_OWNER, UUIDTagType.TYPE, value);
        }

        @Override
        public String getPropertyName() {
            return "Owner";
        }
    };

    public static final Property<String> OWNER_NAME = new Property<String>() {

        @Override
        public boolean has(PersistentDataContainer data) {
            return data.has(HoloItemsAPI.getKeys().CUSTOM_ITEM_OWNER_NAME, PersistentDataType.STRING);
        }

        @Override
        public String get(PersistentDataContainer data) {
            return data.getOrDefault(HoloItemsAPI.getKeys().CUSTOM_ITEM_OWNER_NAME, PersistentDataType.STRING, "Player");
        }

        @Override
        public void set(PersistentDataContainer data, String value) {
            data.set(HoloItemsAPI.getKeys().CUSTOM_ITEM_OWNER_NAME, PersistentDataType.STRING, value);
        }

        @Override
        public String getPropertyName() {
            return "Owner Name";
        }
    };

    public static final Property<Long> COOLDOWN = new Property<Long>() {

        @Override
        public boolean has(PersistentDataContainer data) {
            return data.has(HoloItemsAPI.getKeys().CUSTOM_ITEM_COOLDOWN, PersistentDataType.LONG);
        }

        @Override
        public Long get(PersistentDataContainer data) {
            return data.getOrDefault(HoloItemsAPI.getKeys().CUSTOM_ITEM_COOLDOWN, PersistentDataType.LONG, 0L);
        }

        @Override
        public void set(PersistentDataContainer data, Long value) {
            data.set(HoloItemsAPI.getKeys().CUSTOM_ITEM_COOLDOWN, PersistentDataType.LONG, value);
        }

        @Override
        public String getPropertyName() {
            return "Cooldown";
        }
    };

    public static final Property<Boolean> UNSTACKABLE = new Property<Boolean>() {
        @Override
        public boolean has(PersistentDataContainer data) {
            return data.has(HoloItemsAPI.getKeys().CUSTOM_ITEM_UNSTACK, PersistentDataType.INTEGER);
        }

        @Override
        public Boolean get(PersistentDataContainer data) {
            return has(data);
        }

        @Override
        public void set(PersistentDataContainer data, Boolean value) {
            if (value) {
                data.set(HoloItemsAPI.getKeys().CUSTOM_ITEM_UNSTACK, PersistentDataType.INTEGER, ThreadLocalRandom.current().nextInt());
            } else {
                data.remove(HoloItemsAPI.getKeys().CUSTOM_ITEM_UNSTACK);
            }
        }

        @Override
        public String getPropertyName() {
            return "Unstackable";
        }
    };

    public static final Property<String> ITEM_ID = new Property<String>() {

        @Override
        public boolean has(PersistentDataContainer data) {
            return data.has(HoloItemsAPI.getKeys().CUSTOM_ITEM_ID, PersistentDataType.STRING);
        }

        @Override
        public String get(PersistentDataContainer data) {
            return data.get(HoloItemsAPI.getKeys().CUSTOM_ITEM_ID, PersistentDataType.STRING);
        }

        @Override
        public void set(PersistentDataContainer data, String value) {
            data.set(HoloItemsAPI.getKeys().CUSTOM_ITEM_ID, PersistentDataType.STRING, value);
        }

        @Override
        public String getPropertyName() {
            return "Item ID";
        }
    };

    public static final Property<Integer> RENAMABLE = new Property<Integer>() {
        @Override
        public boolean has(PersistentDataContainer data) {
            return data.has(HoloItemsAPI.getKeys().CUSTOM_ITEM_RENAME, PersistentDataType.INTEGER);
        }

        @Override
        public Integer get(PersistentDataContainer data) {
            return data.getOrDefault(HoloItemsAPI.getKeys().CUSTOM_ITEM_RENAME, PersistentDataType.INTEGER, 0);
        }

        @Override
        public void set(PersistentDataContainer data, Integer value) {
            data.set(HoloItemsAPI.getKeys().CUSTOM_ITEM_RENAME, PersistentDataType.INTEGER, value);
        }

        @Override
        public String getPropertyName() {
            return "Renamed";
        }
    };
}
