package com.strangeone101.holoitems.util;

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

    public static final CustomDamageSource STARFALL = new CustomDamageSource("Starfall", false);
    public static final CustomDamageSource GUN_BASE = new CustomDamageSource("Gun", false);
    public static final CustomDamageSource GUN_COIN = new CustomDamageSource("CoinGun", false);
    public static final CustomDamageSource RUSSIAN_ROULETTE = new CustomDamageSource("RussianRoulette", true);
}
