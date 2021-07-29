package com.strangeone101.holoitemsapi.recipe;

import com.strangeone101.holoitemsapi.HoloItemsAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class RecipeBuilder {

    public static class Shaped {

        private ShapedRecipe recipe;
        private Map<Character, ItemStack> ingredients = new HashMap<>();
        private Map<Character, RecipeChoice> riingredients = new HashMap<>();
        private String[] shape;

        public Shaped(NamespacedKey key, ItemStack stack) {
            this.recipe = new ShapedRecipe(key, stack);
        }

        public Shaped(String key, ItemStack stack) {
            this(new NamespacedKey(HoloItemsAPI.getPlugin(), key), stack);
        }

        public Shaped setIngredient(char character, Material material) {
            ingredients.put(character, new ItemStack(material));
            return this;
        }

        public Shaped setIngredient(char character, ItemStack stack) {
            ingredients.put(character, stack);
            return this;
        }

        public Shaped setIngredient(char character, RecipeChoice recipeChoice) {
            riingredients.put(character, recipeChoice);
            return this;
        }

        public Shaped setShape(String... shape) {
            this.shape = shape;
            return this;
        }

        public ShapedRecipe build() {
            if (shape == null) throw new NoSuchElementException("Shape was not provided!");

            if (ingredients.size() == 0 && riingredients.size() == 0) {
                throw new NoSuchElementException("No ingredients provided!");
            }

            for (Character c : ingredients.keySet()) {
                recipe.setIngredient(c, new RecipeChoice.ExactChoice(ingredients.get(c)));
            }

            for (Character c : riingredients.keySet()) {
                recipe.setIngredient(c, riingredients.get(c));
            }

            return recipe;
        }

        public ShapedRecipe buildRegister() {
            ShapedRecipe recipe = build();
            RecipeManager.registerRecipe(recipe);
            return recipe;
        }
    }

    public static class Shapeless {
        //TODO
    }

    public static class Furnace {
        //TODO
    }

    public static class AdvancedShape {
        //TODO
    }
}
