package com.strangeone101.holoitemsapi.loot.tables;

import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitems.items.Items;
import com.strangeone101.holoitemsapi.loot.BlockLootContext;
import com.strangeone101.holoitemsapi.loot.BlockLootTable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class GemOre implements BlockLootTable {

    public static final CustomItem[] GEMSTONES = {Items.GEM_RUBY, Items.GEM_SAPPHIRE, Items.GEM_AMETHYST, Items.GEM_TOPAZ};

    @Override
    public boolean populateLoot(List<ItemStack> extraDrops, Random random, BlockLootContext context) {
        if (!context.hasSilkTouch()) {
            int rand = random.nextInt(5);
            int fortune = context.getFortune();
            ItemStack stack = new ItemStack(Material.EMERALD);
            if (rand < 4) {
                stack = GEMSTONES[rand].buildStack(context.getPlayer());
            }

            int amount = 1; //Minimum of one
            int rolls = 2 + fortune; //No fortune = 2 chances to drop another item
            int secondChances = random.nextInt(fortune + 1); //Chance for failed rolls to reroll

            //Every roll, we have a 50:50 chance to add more to the amount.
            //If the roll fails, we stop rolling. The amount added is dependant
            //on the amount of fortune.
            for (int rollsLeft = rolls; rollsLeft > 0; rollsLeft--) {
                if (random.nextInt(2) == 0) {
                    amount += 1 + (fortune > 1 ? random.nextInt(fortune) : 0);
                    continue; //Next roll
                }
                if (secondChances > 0) {
                    secondChances--; //Take away a second chance
                    rollsLeft++; //Redo the roll, not just go to the next one
                    continue;
                }
                break; //Roll failed, stop rolling
            }
            stack.setAmount(amount);
            extraDrops.add(stack);

            return false;
        }
        return true;
    }
}
