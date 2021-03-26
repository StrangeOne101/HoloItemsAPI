package com.strangeone101.holoitemsapi;

import org.bukkit.persistence.PersistentDataContainer;

public abstract class Property<T> {

    public abstract boolean has(PersistentDataContainer data);

    public abstract T get(PersistentDataContainer data);

    public abstract void set(PersistentDataContainer data, T value);

    public abstract String getPropertyName();

    @Override
    public int hashCode() {
        return getPropertyName().hashCode();
    }
}
