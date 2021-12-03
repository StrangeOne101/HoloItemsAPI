package com.strangeone101.holoitemsapi.recipe;

import com.strangeone101.holoitemsapi.HoloItemsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;

import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages all custom recipes used with custom items
 */
public class RecipeManager {

    private static Map<NamespacedKey, Recipe> recipes = new HashMap<>();
    private static Map<NamespacedKey, RecipeBuilder.AdvancedRecipe> advanced = new HashMap<>();
    private static Set<Recipe> nonConsumableRecipes = new HashSet<>();

    private static Map<NamespacedKey, NamespacedKey> dummyToAdvancedMap = new HashMap<>(); //Map of all dummy recipes to the advanced recipe

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
        if (Bukkit.getRecipe(key) != null) {
            Bukkit.removeRecipe(key);
        }
        Bukkit.addRecipe(recipe);

        if (recipe instanceof ShapedRecipe) {
            for (RecipeChoice choice : ((ShapedRecipe) recipe).getChoiceMap().values()) {
                if (choice instanceof NonConsumableChoice)
                    nonConsumableRecipes.add(recipe);
                break;
            }
        } else if (recipe instanceof ShapelessRecipe) {
            for (RecipeChoice choice : ((ShapelessRecipe) recipe).getChoiceList()) {
                if (choice instanceof NonConsumableChoice)
                    nonConsumableRecipes.add(recipe);
                break;
            }
        } else if (recipe instanceof SmithingRecipe) {
            if (((SmithingRecipe) recipe).getAddition() instanceof NonConsumableChoice ||
            ((SmithingRecipe) recipe).getBase() instanceof NonConsumableChoice)
                    nonConsumableRecipes.add(recipe);


        }
    }

    public static void registerAdvancedRecipe(Recipe recipe, Recipe dummyRecipe, RecipeBuilder.AdvancedRecipe advancedShape) {
        registerRecipe(recipe);
        registerRecipe(dummyRecipe);
        advanced.put(((Keyed) recipe).getKey(), advancedShape);
        dummyToAdvancedMap.put(((Keyed) dummyRecipe).getKey(), ((Keyed) recipe).getKey());
    }

    public static boolean isAdvancedRecipe(Recipe recipe) {
        if (recipe instanceof Keyed)
            return advanced.containsKey(((Keyed) recipe).getKey());
        return false;
    }

    public static boolean isHiddenRecipe(NamespacedKey key) {
        return dummyToAdvancedMap.containsKey(key);
    }

    public static boolean isHiddenRecipe(Recipe recipe) {
        if (recipe instanceof Keyed)
            return dummyToAdvancedMap.containsKey(((Keyed) recipe).getKey());
        return false;
    }

    public static RecipeBuilder.AdvancedRecipe getAdvancedRecipe(Recipe recipe) {
        if (recipe instanceof Keyed)
            return advanced.get(((Keyed) recipe).getKey());
        return null;
    }

    public static RecipeBuilder.AdvancedRecipe getAdvancedRecipe(NamespacedKey recipe) {
        return advanced.get(recipe);
    }

    public static RecipeBuilder.AdvancedRecipe getAdvancedFromDummy(Recipe recipe) {
        return advanced.get(dummyToAdvancedMap.get(((Keyed) recipe).getKey()));
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
        if (recipe instanceof Keyed)
            return recipes.containsKey(((Keyed) recipe).getKey());
        return false;
    }

    /**
     * Get how many recipes are managed
     * @return The amount
     */
    public static int getRegisteredAmount() {
        return recipes.size();
    }

    /**
     * Gets whether the recipe contains an ingredient that shouldn't
     * be consumed on use
     * @param recipe The recipe
     * @return True if it does
     */
    public static boolean hasNonConsumable(Recipe recipe) {
        return nonConsumableRecipes.contains(recipe);
    }
}
