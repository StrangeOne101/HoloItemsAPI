package com.strangeone101.holoitemsapi.recipe;

import com.strangeone101.holoitemsapi.HoloItemsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.Map;

public class RecipeManager {

    private static Map<NamespacedKey, Recipe> recipes = new HashMap<>();

    public static Recipe getRecipe(NamespacedKey key) {
        return recipes.get(key);
    }

    public static Recipe getRecipe(String key) {
        return recipes.get(new NamespacedKey(HoloItemsAPI.getPlugin(), key));
    }

    public static void registerRecipe(Recipe recipe) {
        if (!(recipe instanceof Keyed)) {
            try {
                throw new InvalidObjectException("Recipe of type " + recipe.getClass().getName() + " does not contain a NameSpace key! Use RecipeManager#registerRecipe(recipe, key) instead!");
            } catch (InvalidObjectException e) {
                e.printStackTrace();
                return;
            }
        }
        registerRecipe(recipe, ((Keyed)recipe).getKey());
    }

    public static void registerRecipe(Recipe recipe, NamespacedKey key) {
        recipes.put(key, recipe);
        if (Bukkit.getRecipe(((Keyed)recipe).getKey()) != null) {
            Bukkit.removeRecipe(((Keyed)recipe).getKey());
        }
        Bukkit.addRecipe(recipe);
    }

    public static void addRecipe(Recipe recipe, NamespacedKey key) {
        recipes.put(key, recipe);
    }

    public static void registerRecipe(Recipe recipe, String key) {
        registerRecipe(recipe, new NamespacedKey(HoloItemsAPI.getPlugin(), key));
    }

    public static void unregisterAll() {
        for (NamespacedKey key : recipes.keySet()) {
            Bukkit.removeRecipe(key);
        }
    }

    public static boolean isManagedRecipe(Recipe recipe) {
        return recipes.values().contains(recipe);
    }

    public static int getRegisteredAmount() {
        return recipes.size();
    }
}
