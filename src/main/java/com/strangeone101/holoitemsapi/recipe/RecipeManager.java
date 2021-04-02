package com.strangeone101.holoitemsapi.recipe;

import com.strangeone101.holoitemsapi.HoloItemsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all custom recipes used with custom items
 */
public class RecipeManager {

    private static Map<NamespacedKey, Recipe> recipes = new HashMap<>();

    /**
     * Get a recipe
     * @param key The namespace key
     * @return The recipe
     */
    public static Recipe getRecipe(NamespacedKey key) {
        return recipes.get(key);
    }

    /**
     * Get a recipe
     * @param key The key
     * @return The recipe
     */
    public static Recipe getRecipe(String key) {
        return recipes.get(new NamespacedKey(HoloItemsAPI.getPlugin(), key));
    }

    /**
     * Register a custom recipe
     * @param recipe The recipe
     */
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

    /**
     * Register a custom recipe
     * @param recipe The recipe
     * @param key The namespace key
     */
    public static void registerRecipe(Recipe recipe, NamespacedKey key) {
        recipes.put(key, recipe);
        if (Bukkit.getRecipe(((Keyed)recipe).getKey()) != null) {
            Bukkit.removeRecipe(((Keyed)recipe).getKey());
        }
        Bukkit.addRecipe(recipe);
    }

    /**
     * Add a recipe to the registry WITHOUT registering it
     * @param recipe The recipe
     * @param key The namespace key
     */
    public static void addRecipe(Recipe recipe, NamespacedKey key) {
        recipes.put(key, recipe);
    }

    /**
     * Register a custom recipe
     * @param recipe The recipe
     * @param key The key
     */
    public static void registerRecipe(Recipe recipe, String key) {
        registerRecipe(recipe, new NamespacedKey(HoloItemsAPI.getPlugin(), key));
    }

    /**
     * Unregister all recipes registered. Internal use only.
     */
    public static void unregisterAll() {
        for (NamespacedKey key : recipes.keySet()) {
            Bukkit.removeRecipe(key);
        }
    }

    /**
     * Get whether the recipe is managed by the registry or not
     * @param recipe The recipe
     * @return True if handled
     */
    public static boolean isManagedRecipe(Recipe recipe) {
        return recipes.values().contains(recipe);
    }

    /**
     * Get how many recipes are managed
     * @return The amount
     */
    public static int getRegisteredAmount() {
        return recipes.size();
    }
}
