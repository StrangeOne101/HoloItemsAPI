package com.strangeone101.holoitems.util;

import com.strangeone101.holoitems.CustomItem;
import com.strangeone101.holoitems.CustomItemRegistry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class CIRecipeChoice extends RecipeChoice.ExactChoice {

    public CIRecipeChoice(ItemStack... stacks) {
        super(stacks);
    }

    @Override
    public boolean test(ItemStack t) {
        for (ItemStack match : this.getChoices()) {
            if (CustomItemRegistry.isCustomItem(match)) {
                CustomItem ci = CustomItemRegistry.getCustomItem(match);
                if (ci != null && ci == CustomItemRegistry.getCustomItem(t) &&
                        (ci.getMaxDurability() == 0 || ci.getDurability(match) == ci.getDurability(t)) &&
                        (match.getDurability() == t.getDurability()))
                    return true;
            }
            else if (match.isSimilar(t))
                return true;
        }
        return false;
    }
}
