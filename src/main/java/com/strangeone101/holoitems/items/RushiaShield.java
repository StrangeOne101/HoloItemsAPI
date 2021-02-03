package com.strangeone101.holoitems.items;

import com.strangeone101.holoitems.CustomItem;
import com.strangeone101.holoitems.HoloItemsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RushiaShield extends CustomItem {

    /** Exceptions for mobs that shouldn't be allowed to be captured due to their size. Boss mobs are already exempt*/
    public static Set<EntityType> EXCEPTIONS = new HashSet<EntityType>(Arrays.asList(EntityType.GHAST, EntityType.ELDER_GUARDIAN, EntityType.GIANT));

    public RushiaShield() {
        super("rushia_shield", Material.SHIELD);
        this.addVariable("mobs", data -> {
            String s = data.get(HoloItemsPlugin.getKeys().RUSHIA_SHIELD_MOBS, PersistentDataType.STRING);
            if (s != null) {
                s = s.replace(";", ", ").replace("_", " ");
            }
            if (s == null || s.equals("")) s = "(None)";
            return s;
        });
    }

    @Override
    public ItemStack buildStack(Player player) {
        ItemStack stack = super.buildStack(player);
        ItemMeta meta = stack.getItemMeta();

        //Add our custom data to built stacks
        meta.getPersistentDataContainer().set(HoloItemsPlugin.getKeys().RUSHIA_SHIELD_MOBS, PersistentDataType.STRING, "");
        meta.getPersistentDataContainer().set(HoloItemsPlugin.getKeys().RUSHIA_SHIELD_COUNT, PersistentDataType.INTEGER, 0);
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        stack.setItemMeta(meta);

        return updateStack(stack, player); //Fix the lore for our added things
    }

    public void killMob(Mob creature, Player player, ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();

        int count = meta.getPersistentDataContainer().get(HoloItemsPlugin.getKeys().RUSHIA_SHIELD_COUNT, PersistentDataType.INTEGER);

        if (count > 2) return;

        String mobString = count == 0 ? creature.getType().name() :
                meta.getPersistentDataContainer().get(HoloItemsPlugin.getKeys().RUSHIA_SHIELD_MOBS, PersistentDataType.STRING) + ";" + creature.getType().name();

        //Update the data on the item
        meta.getPersistentDataContainer().set(HoloItemsPlugin.getKeys().RUSHIA_SHIELD_COUNT, PersistentDataType.INTEGER, count + 1);
        meta.getPersistentDataContainer().set(HoloItemsPlugin.getKeys().RUSHIA_SHIELD_MOBS, PersistentDataType.STRING, mobString);

        stack.setItemMeta(meta);

        updateStack(stack, player);

        Location mediumLocation = creature.getLocation().clone().add(0, creature.getEyeHeight() / 2, 0);

        //This will display some enchantment particles heading towards the player, for a soul absorption effect
        for (int i = 0; i < 16; i++) {
            double xx = Math.random() * 0.6 - 0.6 + mediumLocation.getX(); //Randomize spread of origin location by 0.6 blocks
            double yy = Math.random() * 0.6 - 0.6 + mediumLocation.getY();
            double zz = Math.random() * 0.6 - 0.6 + mediumLocation.getZ();
            player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, player.getEyeLocation().getX(), player.getEyeLocation().getY(), player.getEyeLocation().getZ(), 0,
                    player.getEyeLocation().getX() - xx, player.getEyeLocation().getY() - yy, player.getEyeLocation().getZ() - zz, 1);
        }

        //This message may want to be disabled. Depends on if it gets annoying
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d&kxxx &5Soul Absorbed &d&kxxx"));
    }
}
