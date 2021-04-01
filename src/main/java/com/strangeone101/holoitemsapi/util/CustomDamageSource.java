package com.strangeone101.holoitemsapi.util;

public class CustomDamageSource {

    private String name;
    private boolean ignoreArmor;

    public CustomDamageSource(String name, boolean ignoreArmor) {
        this.name = name.replace(" ", "_");
        this.ignoreArmor = ignoreArmor;
    }

    public boolean doesIgnoreArmor() {
        return ignoreArmor;
    }

    public String getName() {
        return name;
    }
}
