package com.strangeone101.holoitems.items.implementations;

import com.strangeone101.holoitems.Keys;
import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitems.HoloItemsPlugin;
import com.strangeone101.holoitemsapi.interfaces.Interactable;
import com.strangeone101.holoitemsapi.util.CustomDamage;
import com.strangeone101.holoitemsapi.util.CustomDamageSource;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class RussianRevolver extends CustomItem implements Interactable {

    private int shots;

    public RussianRevolver(String name, int shots) {
        super(name, Material.GOLDEN_HOE);
        this.shots = shots;
    }

    @Override
    public ItemStack buildStack(Player player) {
        ItemStack stack = super.buildStack(player);
        ItemMeta meta = stack.getItemMeta();

        //Add our custom data to built stacks
        meta.getPersistentDataContainer().set(Keys.getKeys().RUSSIAN_ROULETTE, PersistentDataType.INTEGER, -1);

        stack.setItemMeta(meta);

        return stack; //Fix the lore for our added things
    }

    public void fire(Player player, ItemStack stack, boolean sneaking) {
        ItemMeta meta = stack.getItemMeta();

        int value = meta.getPersistentDataContainer().getOrDefault(Keys.getKeys().RUSSIAN_ROULETTE, PersistentDataType.INTEGER, -1);

        if (value == -1) {
            if (!sneaking) player.sendMessage(ChatColor.YELLOW + "The barrel needs to be spun first! Sneak right click to spin the barrel!");
            else {
                update(stack, new Random().nextInt(shots) + 1);
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 2, 2); //Reload sound
                player.sendMessage(ChatColor.YELLOW + "You hear the barrel spin!");
            }
        } else if (value == 0) {
            if (!sneaking) player.sendMessage(ChatColor.YELLOW + "Reload the revolver with shift right click first!");
            else {
                update(stack, new Random().nextInt(shots) + 1);
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 2, 2); //Reload sound
                player.sendMessage(ChatColor.YELLOW + "You reload and spin the barrel!");
                broadcastGameMessage(player.getName() + " reloaded and spun the barrel!", player.getLocation(), player);
            }
        } else {
            if (sneaking) {
                player.sendMessage(ChatColor.YELLOW + "You spin the barrel again!");
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 2, 2); //Reload sound
                broadcastGameMessage(player.getName() + " spun the barrel!", player.getLocation(), player);
                return;
            }

            value -= 1;
            update(stack, value);

            if (value == 0) {
                player.sendMessage(ChatColor.YELLOW + "BANG! You pull the trigger and your head explodes!");
                broadcastGameMessage("BANG! " + player.getName() + " pulls the trigger and their head explodes!", player.getLocation(), player);
                ItemStack clone = stack.clone();
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2, 0.1F); //Bang sound
                player.getWorld().dropItem(player.getLocation(), clone); //Drops the item so other players can continue playing regardless of gamerules
                stack.setAmount(0);
                stack.setType(Material.AIR);
                CustomDamage.damageEntity(player, player, 100, CustomDamageSource.RUSSIAN_ROULETTE); //Kill them
            } else {
                player.sendMessage(ChatColor.YELLOW + "CLICK! You pull the trigger and nothing happens!");
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 2, 2);
                broadcastGameMessage("CLICK! " + player.getName() + " pulls the trigger and nothing happens!", player.getLocation(), player);
            }
        }
    }

    private static void update(ItemStack stack, int value) {
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(Keys.getKeys().RUSSIAN_ROULETTE, PersistentDataType.INTEGER, value);
        stack.setItemMeta(meta);
    }

    public static void broadcastGameMessage(String message, Location location, Player ignoredPlayer) {
        double broadcastRange = 16;
        message = ChatColor.GOLD + "(RR) " + ChatColor.YELLOW + message;
        TextComponent text = new TextComponent(message);
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + "You can see this because players near you are playing russian roulette!")));

        for (Player player : location.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(location) <= broadcastRange * broadcastRange && player != ignoredPlayer) {
                player.spigot().sendMessage(text);
            }
        }
    }

    @Override
    public boolean onInteract(Player player, CustomItem item, ItemStack stack) {
        fire(player, stack, player.isSneaking());
        return true;
    }
}
