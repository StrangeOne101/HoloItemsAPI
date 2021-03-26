package com.strangeone101.holoitems;

import com.strangeone101.holoitems.command.HoloItemsCommand;
import com.strangeone101.holoitems.items.Items;
import com.strangeone101.holoitemsapi.loot.CustomLootRegistry;
import com.strangeone101.holoitemsapi.loot.tables.Endermite;
import com.strangeone101.holoitemsapi.loot.tables.GemOre;
import com.strangeone101.holoitemsapi.loot.tables.Spawner;
import com.strangeone101.holoitemsapi.recipe.CIRecipeChoice;
import com.strangeone101.holoitemsapi.recipe.RecipeManager;
import com.strangeone101.holoitemsapi.CustomItemRegistry;
import com.strangeone101.holoitemsapi.HoloItemsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class HoloItemsPlugin extends JavaPlugin {

    public static HoloItemsPlugin INSTANCE;

    @Override
    public void onEnable() {


        INSTANCE = this;


        getCommand("holoitem").setExecutor(new HoloItemsCommand());

        HoloItemsAPI.setup(this);

        Items.registerHoloItems();
        registerRecipes();

        CustomLootRegistry.registerDeathTable(EntityType.ENDERMITE, new Endermite());
        CustomLootRegistry.registerBlockBreakTable(Material.SPAWNER, new Spawner());
        CustomLootRegistry.registerBlockBreakTable(Material.EMERALD_ORE, new GemOre());



        getLogger().info("Registered " + CustomItemRegistry.getCustomItems().size()
                + " custom items and " + RecipeManager.getRegisteredAmount() + " custom recipes!");

        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        HoloItemsAPI.shutdown();
    }

    private void registerRecipes() {
        ItemStack rushiaShield = Items.RUSHIA_SHIELD.buildStack(null);
        ShapedRecipe rushiaShieldRecipe = new ShapedRecipe(new NamespacedKey(this, "rushia_shield"), rushiaShield);
        rushiaShieldRecipe.shape("WSW", "WWW", " W ");
        rushiaShieldRecipe.setIngredient('W', Material.WARPED_PLANKS);
        rushiaShieldRecipe.setIngredient('S', Material.NETHERITE_INGOT);

        RecipeManager.registerRecipe(rushiaShieldRecipe);

        //Storing the recipe that allows shields to be decorated
        RecipeManager.addRecipe(Bukkit.getRecipe(NamespacedKey.minecraft("shield_decoration")), (NamespacedKey.minecraft("shield_decoration")));

        ItemStack etherreal_essense = Items.ETHERREAL_ESSENSE.buildStack(null);
        ItemStack otherworldly_adhesive = Items.OTHERWORLDLY_ADHESIVE.buildStack(null);
        ItemStack nether_diamond = Items.NETHER_DIAMOND.buildStack(null);
        ItemStack enchanted_sand = Items.ENCHANTED_SAND.buildStack(null);

        ShapelessRecipe adhesiveRecipe = new ShapelessRecipe(new NamespacedKey(this, "otherworldly_adhesive"), otherworldly_adhesive);
        adhesiveRecipe.addIngredient(Material.WHEAT);
        adhesiveRecipe.addIngredient(Material.WATER_BUCKET);
        adhesiveRecipe.addIngredient(Material.SAND);
        adhesiveRecipe.addIngredient(new CIRecipeChoice(etherreal_essense));

        RecipeManager.registerRecipe(adhesiveRecipe, new NamespacedKey(this, "adhesiveRecipe"));

        ShapelessRecipe netherDiamondRecipe = new ShapelessRecipe(new NamespacedKey(this, "nether_diamond"), nether_diamond);
        netherDiamondRecipe.addIngredient(Material.DIAMOND);
        netherDiamondRecipe.addIngredient(Material.NETHERITE_INGOT);
        netherDiamondRecipe.addIngredient(Material.NETHER_STAR);
        netherDiamondRecipe.addIngredient(new CIRecipeChoice(otherworldly_adhesive));

        RecipeManager.registerRecipe(netherDiamondRecipe, new NamespacedKey(this, "nether_diamond"));

        ShapedRecipe enchantedSandRecipe = new ShapedRecipe(new NamespacedKey(this, "enchanted_sand"), enchanted_sand);
        enchantedSandRecipe.shape("SSS", "SXS", "SSS");
        enchantedSandRecipe.setIngredient('S', new RecipeChoice.ExactChoice(new ItemStack(Material.SAND, 64)));
        enchantedSandRecipe.setIngredient('X', new CIRecipeChoice(nether_diamond));

        RecipeManager.registerRecipe(enchantedSandRecipe, new NamespacedKey(this, "enchanted_sand"));



    }
}
