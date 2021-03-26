package com.strangeone101.holoitems.items;

import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitems.items.implementations.BerryTrident;
import com.strangeone101.holoitems.items.implementations.EnchantedBlock;
import com.strangeone101.holoitems.items.implementations.MegaSponge;
import com.strangeone101.holoitems.items.implementations.MoguBoots;
import com.strangeone101.holoitems.items.implementations.RushiaShield;
import com.strangeone101.holoitems.items.implementations.RussianRevolver;
import com.strangeone101.holoitems.items.implementations.ScrambledEgg;
import org.bukkit.ChatColor;
import org.bukkit.Material;

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
    public static CustomItem RUSSIAN_ROULETTE_REVOLVER_LARGE;
    public static CustomItem GEM_RUBY;
    public static CustomItem GEM_SAPPHIRE;
    public static CustomItem GEM_TOPAZ;
    public static CustomItem GEM_AMETHYST;
    public static CustomItem SPONGE_LARGE;
    public static CustomItem SPONGE_LARGE_WET;
    public static CustomItem SPONGE_LAVA;
    public static CustomItem SPONGE_LAVA_WET;

    public static CustomItem SCRAMBLED_EGG;
    public static CustomItem BUTTER_CLOCK;


    public static void registerHoloItems() {

        RUSHIA_SHIELD = (RushiaShield) new RushiaShield().setInternalID(1700).register();
        //BERRY_TRIDENT = new BerryTrident();
        ETHERREAL_ESSENSE = new CustomItem("void_essense", Material.PURPLE_DYE)
                .setDisplayName(ChatColor.LIGHT_PURPLE + "Void Essense")
                .addLore(ChatColor.DARK_GRAY + "Crafting Ingredient").addLore("")
                .addLore(ChatColor.GRAY + "Mysterious...? What use could it have?")
                .setInternalID(1010).register();
        OTHERWORLDLY_ADHESIVE = new CustomItem("otherworldly_adhesive", Material.BLAZE_POWDER)
                .setDisplayName(ChatColor.LIGHT_PURPLE + "Ethereal Adhesive")
                .addLore(ChatColor.DARK_GRAY + "Crafting Ingredient").addLore("")
                .addLore(ChatColor.GRAY + "You feel a mysterious force pulling")
                .addLore(ChatColor.GRAY + "things towards it").setInternalID(1011).register();
        NETHER_DIAMOND = new CustomItem("nether_diamond", Material.DIAMOND)
                .setDisplayName(ChatColor.RED + "Nether Diamond")
                .addLore(ChatColor.DARK_GRAY + "Crafting Ingredient").addLore("")
                .addLore(ChatColor.GRAY + "Hot to the touch").setInternalID(1012).register();
        ENCHANTED_SAND = new EnchantedBlock("enchanted_sand", Material.SAND, "sand")
                .setDisplayName(ChatColor.LIGHT_PURPLE + "Enchanted Sand").setInternalID(2100).register();
        ENCHANTED_DIRT = new EnchantedBlock("enchanted_dirt", Material.DIRT, "dirt")
                .setDisplayName(ChatColor.LIGHT_PURPLE + "Enchanted Dirt").setInternalID(2101).register();
        ENCHANTED_STONE = new EnchantedBlock("enchanted_stone", Material.STONE, "stone")
                .setDisplayName(ChatColor.LIGHT_PURPLE + "Enchanted Stone").setInternalID(2102).register();
        MOGU_BOOTS = new MoguBoots().setInternalID(1701).register();
        RUSSIAN_ROULETTE_REVOLVER = new RussianRevolver("russian_roulette_revolver", 6)
                .setDisplayName(ChatColor.YELLOW + "1967 Soviet Russian Revolver")
                .addLore(ChatColor.GRAY + "Great for playing russian roulette!").addLore("")
                .addLore(ChatColor.GOLD + "Shift right click: " + ChatColor.YELLOW + "Spin barrel")
                .addLore(ChatColor.GOLD + "Right click: " + ChatColor.YELLOW + "Fire one in the chamber").setInternalID(1500).register();
        RUSSIAN_ROULETTE_REVOLVER_LARGE = new RussianRevolver("russian_roulette_revolver_extended", 12)
                .setDisplayName(ChatColor.YELLOW + "1968 Extended Soviet Russian Revolver")
                .addLore(ChatColor.GRAY + "Great for playing russian roulette!")
                .addLore(ChatColor.GRAY + "The extended barrel fits 12 bullets.").addLore("")
                .addLore(ChatColor.GOLD + "Shift right click: " + ChatColor.YELLOW + "Spin barrel")
                .addLore(ChatColor.GOLD + "Right click: " + ChatColor.YELLOW + "Fire one in the chamber").setInternalID(1501).register();

        GEM_RUBY = new CustomItem("gem_ruby", Material.EMERALD, ChatColor.DARK_RED + "Ruby").setInternalID(1000).register();
        GEM_SAPPHIRE = new CustomItem("gem_sapphire", Material.EMERALD, ChatColor.BLUE + "Sapphire").setInternalID(1001).register();
        GEM_TOPAZ = new CustomItem("gem_topaz", Material.EMERALD, ChatColor.GOLD + "Topaz").setInternalID(1004).register();
        GEM_AMETHYST = new CustomItem("gem_amethyst", Material.EMERALD, ChatColor.LIGHT_PURPLE + "Amethyst").setInternalID(1003).register();

        SPONGE_LARGE_WET = new CustomItem("sponge_large_wet", Material.WET_SPONGE, ChatColor.YELLOW + "Large Sponge (Full)")
                .addLore(ChatColor.DARK_GRAY + "Block").addLore("").addLore(ChatColor.GRAY + "Soaks up a large amount of water.")
                .addLore("").addLore(ChatColor.RED + "Dry it in a furnace!")
        .setInternalID(2120).register();

        SPONGE_LARGE = new MegaSponge("sponge_large", 11, Material.WATER, SPONGE_LARGE_WET)
                .setDisplayName(ChatColor.YELLOW + "Large Sponge").addLore(ChatColor.DARK_GRAY + "Placeable Block").addLore("")
                .addLore(ChatColor.GRAY + "Soaks up nearby lava").setInternalID(2121).register();

        SPONGE_LAVA_WET = new CustomItem("sponge_lava_wet", Material.WET_SPONGE, ChatColor.RED + "Lava Sponge (Full)")
                .addLore(ChatColor.DARK_GRAY + "Block").addLore("").addLore(ChatColor.GRAY + "Soaks up nearby lava")
                .addLore("").addLore(ChatColor.YELLOW + "Must be emptied!")
                .setInternalID(2122).register();

        SPONGE_LAVA = new MegaSponge("sponge_lava", 5, Material.LAVA, SPONGE_LAVA_WET)
                .setDisplayName(ChatColor.RED + "Lava Sponge").addLore(ChatColor.DARK_GRAY + "Placeable Block").addLore("")
                .addLore(ChatColor.GRAY + "Soaks up a large amount of lava").setInternalID(2123).register();

        SCRAMBLED_EGG = new ScrambledEgg("scrambled_egg", Material.BAKED_POTATO).setDisplayName(ChatColor.YELLOW + "Scrambled Egg").setInternalID(501).register();
        BUTTER_CLOCK = new ScrambledEgg("butter_clock", Material.CLOCK).setDisplayName(ChatColor.YELLOW + "Butter Clock").setInternalID(502).register();

    }
}
