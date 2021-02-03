package com.strangeone101.holoitems;

import com.strangeone101.holoitems.command.HoloItemsCommand;
import com.strangeone101.holoitems.listener.AbilityListener;
import com.strangeone101.holoitems.listener.ItemListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class HoloItemsPlugin extends JavaPlugin {

    public static HoloItemsPlugin INSTANCE;

    public static List<NamespacedKey> recipes = new ArrayList<NamespacedKey>();

    private Keys keys;

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled!");

        INSTANCE = this;
        this.keys = new Keys();

        getCommand("holoitem").setExecutor(new HoloItemsCommand());

        Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new AbilityListener(), this);

        CustomItemRegistry.registerHoloItems();
        registerRecipes();

        //Make all item abilities tick
        new ItemAbility.CustomItemAbilityTask().runTaskTimer(this, 1L, 1L);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        for (NamespacedKey recipe : recipes) {
            Bukkit.removeRecipe(recipe);
        }

        ItemAbility.removeAll();
    }

    private void registerRecipes() {
        ItemStack rushiaShield = CustomItemRegistry.RUSHIA_SHIELD.buildStack(null);
        ShapedRecipe rushiaShieldRecipe = new ShapedRecipe(new NamespacedKey(this, "rushia_shield"), rushiaShield);
        rushiaShieldRecipe.shape("WSW", "WWW", " W ");
        rushiaShieldRecipe.setIngredient('W', Material.WARPED_PLANKS);
        rushiaShieldRecipe.setIngredient('S', Material.NETHERITE_INGOT);

        if (Bukkit.getRecipe(new NamespacedKey(this, "rushia_shield")) == null) {
            Bukkit.addRecipe(rushiaShieldRecipe);
            recipes.add(new NamespacedKey(this, "rushia_shield"));
        }
    }

    public static Keys getKeys() {
        return INSTANCE.keys;
    }
}
