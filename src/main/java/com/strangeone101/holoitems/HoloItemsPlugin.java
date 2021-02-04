package com.strangeone101.holoitems;

import com.strangeone101.holoitems.command.HoloItemsCommand;
import com.strangeone101.holoitems.items.Items;
import com.strangeone101.holoitems.listener.AbilityListener;
import com.strangeone101.holoitems.listener.ItemListener;
import com.strangeone101.holoitems.listener.LootListener;
import com.strangeone101.holoitems.util.CIRecipeChoice;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class HoloItemsPlugin extends JavaPlugin {

    public static HoloItemsPlugin INSTANCE;

    public static Map<NamespacedKey, Recipe> recipes = new HashMap<NamespacedKey, Recipe>();

    private Keys keys;

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled!");

        INSTANCE = this;
        this.keys = new Keys();

        getCommand("holoitem").setExecutor(new HoloItemsCommand());

        Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new AbilityListener(), this);
        Bukkit.getPluginManager().registerEvents(new LootListener(), this);

        Items.registerHoloItems();
        registerRecipes();

        //Make all item abilities tick
        new ItemAbility.CustomItemAbilityTask().runTaskTimer(this, 1L, 1L);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        for (NamespacedKey recipe : recipes.keySet()) {
            Bukkit.removeRecipe(recipe);
        }

        ItemAbility.removeAll();
    }

    private void registerRecipes() {
        ItemStack rushiaShield = Items.RUSHIA_SHIELD.buildStack(null);
        ShapedRecipe rushiaShieldRecipe = new ShapedRecipe(new NamespacedKey(this, "rushia_shield"), rushiaShield);
        rushiaShieldRecipe.shape("WSW", "WWW", " W ");
        rushiaShieldRecipe.setIngredient('W', Material.WARPED_PLANKS);
        rushiaShieldRecipe.setIngredient('S', Material.NETHERITE_INGOT);

        if (Bukkit.getRecipe(new NamespacedKey(this, "rushia_shield")) != null) {
            Bukkit.removeRecipe(new NamespacedKey(this, "rushia_shield"));
        }
        Bukkit.addRecipe(rushiaShieldRecipe);
        recipes.put(new NamespacedKey(this, "rushia_shield"), rushiaShieldRecipe);


        //Storing the recipe that allows shields to be decorated
        recipes.put(NamespacedKey.minecraft("shield_decoration"), Bukkit.getRecipe(NamespacedKey.minecraft("shield_decoration")));

        ItemStack etherreal_essense = Items.ETHERREAL_ESSENSE.buildStack(null);
        ItemStack otherworldly_adhesive = Items.OTHERWORLDLY_ADHESIVE.buildStack(null);
        ItemStack nether_diamond = Items.NETHER_DIAMOND.buildStack(null);
        ItemStack enchanted_sand = Items.ENCHANTED_SAND.buildStack(null);

        ShapelessRecipe adhesiveRecipe = new ShapelessRecipe(new NamespacedKey(this, "otherworldly_adhesive"), otherworldly_adhesive);
        adhesiveRecipe.addIngredient(Material.WHEAT);
        adhesiveRecipe.addIngredient(Material.WATER_BUCKET);
        adhesiveRecipe.addIngredient(Material.SAND);
        adhesiveRecipe.addIngredient(new CIRecipeChoice(etherreal_essense));
        if (Bukkit.getRecipe(new NamespacedKey(this, "otherworldly_adhesive")) != null) {
            Bukkit.removeRecipe(new NamespacedKey(this, "otherworldly_adhesive"));
        }
        Bukkit.addRecipe(adhesiveRecipe);
        recipes.put(new NamespacedKey(this, "adhesiveRecipe"), adhesiveRecipe);

        ShapelessRecipe netherDiamondRecipe = new ShapelessRecipe(new NamespacedKey(this, "nether_diamond"), nether_diamond);
        netherDiamondRecipe.addIngredient(Material.DIAMOND);
        netherDiamondRecipe.addIngredient(Material.NETHERITE_INGOT);
        netherDiamondRecipe.addIngredient(Material.NETHER_STAR);
        netherDiamondRecipe.addIngredient(new CIRecipeChoice(otherworldly_adhesive));
        if (Bukkit.getRecipe(new NamespacedKey(this, "nether_diamond")) != null) {
            Bukkit.removeRecipe(new NamespacedKey(this, "nether_diamond"));
        }
        Bukkit.addRecipe(netherDiamondRecipe);
        recipes.put(new NamespacedKey(this, "nether_diamond"), netherDiamondRecipe);

        ShapedRecipe enchantedSandRecipe = new ShapedRecipe(new NamespacedKey(this, "enchanted_sand"), enchanted_sand);
        enchantedSandRecipe.shape("SSS", "SXS", "SSS");
        enchantedSandRecipe.setIngredient('S', new RecipeChoice.ExactChoice(new ItemStack(Material.SAND, 64)));
        enchantedSandRecipe.setIngredient('X', new CIRecipeChoice(nether_diamond));
        if (Bukkit.getRecipe(new NamespacedKey(this, "enchanted_sand")) != null) {
            Bukkit.removeRecipe(new NamespacedKey(this, "enchanted_sand"));
        }
        Bukkit.addRecipe(enchantedSandRecipe);
        recipes.put(new NamespacedKey(this, "enchanted_sand"), enchantedSandRecipe);



    }

    public static Keys getKeys() {
        return INSTANCE.keys;
    }
}
