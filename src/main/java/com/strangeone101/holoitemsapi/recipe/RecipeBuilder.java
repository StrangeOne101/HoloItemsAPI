package com.strangeone101.holoitemsapi.recipe;

import com.strangeone101.holoitemsapi.CustomItemRegistry;
import com.strangeone101.holoitemsapi.HoloItemsAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

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

            recipe.shape(shape);

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

        private ShapelessRecipe recipe;
        private Map<ItemStack, Integer> ingredients = new HashMap<>();
        private Map<RecipeChoice, Integer> riingredients = new HashMap<>();
        private int totalSlots = 0;

        public Shapeless(NamespacedKey key, ItemStack stack) {
            this.recipe = new ShapelessRecipe(key, stack);
        }

        public Shapeless(String key, ItemStack stack) {
            this(new NamespacedKey(HoloItemsAPI.getPlugin(), key), stack);
        }

        public Shapeless addIngredient(Material material) {
            ingredients.put(new ItemStack(material), 1);
            totalSlots++;
            return this;
        }

        public Shapeless addIngredient(ItemStack stack) {
            ingredients.put(stack, 1);
            totalSlots++;
            return this;
        }

        public Shapeless addIngredient(RecipeChoice recipeChoice) {
            riingredients.put(recipeChoice, 1);
            totalSlots++;
            return this;
        }

        public Shapeless addIngredient(Material material, int amount) {
            ingredients.put(new ItemStack(material), amount);
            totalSlots += amount;
            return this;
        }

        public Shapeless addIngredient(ItemStack stack, int amount) {
            ingredients.put(stack, amount);
            totalSlots += amount;
            return this;
        }

        public Shapeless setIngredient(RecipeChoice recipeChoice, int amount) {
            riingredients.put(recipeChoice, amount);
            totalSlots += amount;
            return this;
        }

        public ShapelessRecipe build() {
            if (totalSlots > 9) throw new NoSuchElementException("Provided more than 9 slots!");

            if (ingredients.size() == 0 && riingredients.size() == 0) {
                throw new NoSuchElementException("No ingredients provided!");
            }

            for (ItemStack stack : ingredients.keySet()) {
                for (int i = 0; i < ingredients.get(stack); i++)
                    recipe.addIngredient(new RecipeChoice.ExactChoice(stack));

            }

            for (RecipeChoice c : riingredients.keySet()) {
                recipe.addIngredient(c);
            }

            return recipe;
        }

        public ShapelessRecipe buildRegister() {
            ShapelessRecipe recipe = build();
            RecipeManager.registerRecipe(recipe);
            return recipe;
        }
    }

    public static class Furnace {
        //TODO
    }

    public static class AdvancedShape extends AdvancedRecipe {

        private ShapedRecipe recipe;
        private Map<Character, ItemStack> ingredients = new HashMap<>();
        private Map<Character, RecipeChoice> riingredients = new HashMap<>();
        private Map<Character, RecipeGroup> groups = new HashMap<>();
        private Map<Byte, RecipeGroup> indexedGroups = new HashMap<>();
        private String[] shape;
        private int firstNotEmpty = -1;


        public AdvancedShape(String key, ItemStack output) {
            super(key, output);
        }

        public AdvancedShape setGroupFilter(RecipeGroup group, CIRecipeChoice choice) {
            this.filters.put(group, choice);
            return this;
        }

        public AdvancedShape setGroupItems(RecipeGroup group, ItemStack... itemstacks) {
            this.filters.put(group, new CIRecipeChoice(itemstacks));
            return this;
        }

        public AdvancedShape setGroupItems(RecipeGroup group, Material... materials) {
            ItemStack[] stacks = new ItemStack[materials.length];
            for (int i = 0; i < materials.length; i++) {
                stacks[i] = new ItemStack(materials[i]);
            }
            this.filters.put(group, new CIRecipeChoice(stacks));
            return this;
        }

        public AdvancedShape setIngredientGroup(char character, RecipeGroup group) {
            this.groups.put(character, group);
            return this;
        }

        public AdvancedShape setIngredient(char character, Material material) {
            ingredients.put(character, new ItemStack(material));
            return this;
        }

        public AdvancedShape setIngredient(char character, ItemStack stack) {
            ingredients.put(character, stack);
            return this;
        }

        public AdvancedShape setIngredient(char character, RecipeChoice recipeChoice) {
            riingredients.put(character, recipeChoice);
            return this;
        }

        public AdvancedShape setShape(String... shape) {
            this.shape = shape;
            return this;
        }

        public String[] getShape() {
            return this.shape;
        }

        public int getFirstNotEmpty() {
            return firstNotEmpty;
        }

        public boolean checkStack(char ingredientChar, ItemStack stack) {
            if (ingredients.containsKey(ingredientChar)) {
                if (CustomItemRegistry.isCustomItem(ingredients.get(ingredientChar)) && CustomItemRegistry.isCustomItem(stack)) {
                    return CustomItemRegistry.getCustomItem(ingredients.get(ingredientChar)).getInternalID() == CustomItemRegistry.getCustomItem(stack).getInternalID();
                } else {
                    return ingredients.get(ingredientChar).isSimilar(stack);
                }
            } else if (riingredients.containsKey(ingredientChar)) {
                return riingredients.get(ingredientChar).test(stack);
            } else if (groups.containsKey(ingredientChar)) {
                return this.filters.get(groups.get(ingredientChar)).test(stack);
            }
            return false;
        }

        public AdvancedShape previewModifier(RecipeModifier modifier) {
            this.preview = modifier;
            return this;
        }

        public AdvancedShape craftModifier(RecipeModifier modifier) {
            this.craft = modifier;
            return this;
        }

        public AdvancedShape enableRecipeBookPreviews() {
            this.previewInRecipeBook = true;
            return this;
        }

        public ShapedRecipe[] buildRegister() {
            if (shape == null) throw new NoSuchElementException("Shape was not provided!");

            if (ingredients.size() == 0 && riingredients.size() == 0 && filters.size() == 0) {
                throw new NoSuchElementException("No ingredients provided!");
            }

            if (previewInRecipeBook) {
                //TODO Make 20 recipes with different inputs from the groups
                //before grouping them together
            } else {
                ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(HoloItemsAPI.getPlugin(), key), output);

                ItemStack dummyItem = new ItemStack(Material.KNOWLEDGE_BOOK);
                ItemMeta dummyMeta = dummyItem.getItemMeta();
                dummyMeta.setDisplayName(recipe.getKey().getKey());
                dummyItem.setItemMeta(dummyMeta);
                ShapedRecipe dummyRecipe = new ShapedRecipe(new NamespacedKey(HoloItemsAPI.getPlugin(), "__" + key), dummyItem);

                recipe.shape(shape);
                dummyRecipe.shape(shape);

                for (Character c : ingredients.keySet()) {
                    recipe.setIngredient(c, new RecipeChoice.ExactChoice(ingredients.get(c)));
                    dummyRecipe.setIngredient(c, new RecipeChoice.ExactChoice(ingredients.get(c)));
                }

                for (Character c : riingredients.keySet()) {
                    recipe.setIngredient(c, riingredients.get(c));
                    dummyRecipe.setIngredient(c, riingredients.get(c));
                }

                for (Character c : groups.keySet()) {
                    RecipeGroup g = groups.get(c);
                    recipe.setIngredient(c, filters.get(g)); //The proper item it accepts
                    dummyRecipe.setIngredient(c, filters.get(g).getItemStack().getType()); //The dummy item without any NBT

                    outter:
                    for (int row = 0; row < 3 && row < shape.length; row++) {
                        for (int col = 0; col < 3 && col < shape[row].length(); col++) {
                            if (shape[row].charAt(col) == c) {
                                indexedGroups.put((byte) (row * shape.length + col), g);
                                break outter;
                            }
                        }
                    }

                }

                outter:
                for (int row = 0; row < 3 && row < shape.length; row++) {
                    for (int col = 0; col < 3 && col < shape[row].length(); col++) {
                        if (shape[row].charAt(col) != ' ') {
                            firstNotEmpty = row * shape.length + col;
                            break outter;
                        }
                    }
                }

                RecipeManager.registerAdvancedRecipe(recipe, dummyRecipe, this);

                return new ShapedRecipe[] {recipe};
            }


            return null;
        }

        @Override
        public RecipeContext buildContext(CraftingInventory craftingTable, ClickType type) {
            RecipeContext context = new RecipeContext();
            context.setPlayer((Player) craftingTable.getViewers().get(0));
            context.setRecipe(craftingTable.getRecipe());
            context.setLocation(craftingTable.getViewers().get(0).getLocation());
            context.setClickType(type);
            context.setWorld(context.getLocation().getWorld());

            return context;
        }

        @Override
        public Map<RecipeGroup, ItemStack> getInputItems(CraftingInventory craftingInventory) {
            int size = craftingInventory.getSize() == 9 ? 3 : 2;
            int offset = 0;
            outter:
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    int currIndex = row * size + col;
                    if (craftingInventory.getMatrix()[currIndex] != null) {
                        offset = currIndex - firstNotEmpty;
                        break outter;
                    }
                }
            }

            Map<RecipeGroup, ItemStack> stacks = new HashMap<>();

            for (byte index : indexedGroups.keySet()) {
                int newIndex = offset + index;

                stacks.put(indexedGroups.get(index), craftingInventory.getMatrix()[newIndex]);
            }

            return stacks;
        }

        //TODO
    }

    public static abstract class AdvancedRecipe {
        protected boolean previewInRecipeBook;
        protected String key;
        protected ItemStack output;
        protected RecipeModifier preview, craft = (item, map, context) -> item;
        protected Map<RecipeGroup, CIRecipeChoice> filters = new HashMap<>();

        protected AdvancedRecipe(String key, ItemStack output) {
            this.key = key;
            this.output = output;
        }

        public Map<RecipeGroup, CIRecipeChoice> getFilters() {
            return filters;
        }

        public boolean shouldPreviewInRecipeBook() {
            return previewInRecipeBook;
        }

        public ItemStack getOutput() {
            return output;
        }

        public RecipeModifier getCraftModifier() {
            return craft;
        }

        public RecipeModifier getPreviewModifier() {
            return preview;
        }

        public RecipeContext buildContext(CraftingInventory craftingTable, ClickType click) {

            craftingTable.getMatrix();
            return null;
        }

        public abstract Map<RecipeGroup, ItemStack> getInputItems(CraftingInventory craftingInventory);
    }
}
