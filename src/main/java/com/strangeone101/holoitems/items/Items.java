package com.strangeone101.holoitems.items;

import com.strangeone101.holoitems.CustomItem;
import com.strangeone101.holoitems.CustomItemRegistry;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class Items {

    public static RushiaShield RUSHIA_SHIELD;
    public static BerryTrident BERRY_TRIDENT;
    public static CustomItem ETHERREAL_ESSENSE;
    public static CustomItem OTHERWORLDLY_ADHESIVE;
    public static CustomItem NETHER_DIAMOND;
    public static CustomItem ENCHANTED_SAND;

    public static void registerHoloItems() {

        RUSHIA_SHIELD = new RushiaShield();
        BERRY_TRIDENT = new BerryTrident();
        ETHERREAL_ESSENSE = new CustomItem("etherreal_essense", Material.PURPLE_DYE)
                .setDisplayName(ChatColor.LIGHT_PURPLE + "Etherreal Essense")
                .addLore(ChatColor.GRAY + "Mysterious...? What use could it have?");
        OTHERWORLDLY_ADHESIVE = new CustomItem("otherworldly_adhesive", Material.BLAZE_POWDER)
                .setDisplayName(ChatColor.LIGHT_PURPLE + "Otherwordly Adhesive")
                .addLore(ChatColor.GRAY + "You feel a mysterious force pulling")
                .addLore("things towards it");
        NETHER_DIAMOND = new CustomItem("nether_diamond", Material.DIAMOND)
                .setDisplayName(ChatColor.RED + "Nether Diamond")
                .addLore(ChatColor.GRAY + "Hot to the touch");
        ENCHANTED_SAND = new EnchantedBlock("enchanted_sand", Material.SAND, "sand")
                .setDisplayName(ChatColor.LIGHT_PURPLE + "Enchanted Sand");

        CustomItemRegistry.register(RUSHIA_SHIELD);
        CustomItemRegistry.register(BERRY_TRIDENT);
        CustomItemRegistry.registerBlankId(); //Leave this one for thrown berry tridents
        CustomItemRegistry.register(ETHERREAL_ESSENSE);
        CustomItemRegistry.register(OTHERWORLDLY_ADHESIVE);
        CustomItemRegistry.register(NETHER_DIAMOND);
        CustomItemRegistry.register(ENCHANTED_SAND);
    }
}
