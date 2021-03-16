package com.strangeone101.holoitems.items;

import com.strangeone101.holoitems.CustomItem;
import com.strangeone101.holoitems.CustomItemRegistry;
import com.strangeone101.holoitems.items.implementations.BerryTrident;
import com.strangeone101.holoitems.items.implementations.EnchantedBlock;
import com.strangeone101.holoitems.items.implementations.MoguBoots;
import com.strangeone101.holoitems.items.implementations.RushiaShield;
import com.strangeone101.holoitems.items.implementations.RushianRevolver;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;

public class Items {

    public static RushiaShield RUSHIA_SHIELD;
    public static BerryTrident BERRY_TRIDENT;
    public static CustomItem ETHERREAL_ESSENSE;
    public static CustomItem OTHERWORLDLY_ADHESIVE;
    public static CustomItem NETHER_DIAMOND;
    public static CustomItem ENCHANTED_SAND;
    public static CustomItem ENCHANTED_DIRT;
    public static CustomItem ENCHANTED_STONE;
    public static CustomItem MOGU_BOOTS;
    public static CustomItem RUSSIAN_ROULETTE_REVOLVER;
    public static CustomItem GEM_RUBY;
    public static CustomItem GEM_SAPPHIRE;
    public static CustomItem GEM_TOPAZ;
    public static CustomItem GEM_AMETHYST;

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
        ENCHANTED_DIRT = new EnchantedBlock("enchanted_dirt", Material.DIRT, "dirt")
                .setDisplayName(ChatColor.LIGHT_PURPLE + "Enchanted Dirt");
        ENCHANTED_STONE = new EnchantedBlock("enchanted_stone", Material.STONE, "stone")
                .setDisplayName(ChatColor.LIGHT_PURPLE + "Enchanted Stone");
        MOGU_BOOTS = new MoguBoots();
        RUSSIAN_ROULETTE_REVOLVER = new RushianRevolver();

        GEM_RUBY = new CustomItem("gem_ruby", Material.EMERALD, ChatColor.DARK_RED + "Ruby");
        GEM_SAPPHIRE = new CustomItem("gem_sapphire", Material.EMERALD, ChatColor.BLUE + "Sapphire");
        GEM_TOPAZ = new CustomItem("gem_topaz", Material.EMERALD, ChatColor.GOLD + "Topaz");
        GEM_AMETHYST = new CustomItem("gem_amethyst", Material.EMERALD, ChatColor.LIGHT_PURPLE + "Amethyst");

        CustomItemRegistry.register(RUSHIA_SHIELD);
        CustomItemRegistry.register(BERRY_TRIDENT);
        CustomItemRegistry.registerBlankId(); //Leave this one for thrown berry tridents
        CustomItemRegistry.register(ETHERREAL_ESSENSE);
        CustomItemRegistry.register(OTHERWORLDLY_ADHESIVE);
        CustomItemRegistry.register(NETHER_DIAMOND);
        CustomItemRegistry.register(ENCHANTED_SAND);
        CustomItemRegistry.register(ENCHANTED_DIRT);
        CustomItemRegistry.register(ENCHANTED_STONE);
        CustomItemRegistry.register(MOGU_BOOTS);
        CustomItemRegistry.register(RUSSIAN_ROULETTE_REVOLVER);
        CustomItemRegistry.register(GEM_RUBY);
        CustomItemRegistry.register(GEM_SAPPHIRE);
        CustomItemRegistry.register(GEM_TOPAZ);
        CustomItemRegistry.register(GEM_AMETHYST);


    }
}
