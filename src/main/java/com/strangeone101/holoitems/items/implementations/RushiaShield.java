package com.strangeone101.holoitems.items.implementations;

import com.strangeone101.holoitems.Keys;
import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitems.HoloItemsPlugin;
import com.strangeone101.holoitemsapi.Properties;
import com.strangeone101.holoitems.abilities.RushiaShieldAbility;
import com.strangeone101.holoitemsapi.interfaces.Interactable;
import com.strangeone101.holoitemsapi.interfaces.ItemEvent;
import com.strangeone101.holoitemsapi.EventContext;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Boss;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RushiaShield extends CustomItem implements Interactable {

    /** Exceptions for mobs that shouldn't be allowed to be captured due to their size. Boss mobs are already exempt*/
    public static Set<EntityType> EXCEPTIONS = new HashSet<>(Arrays.asList(EntityType.GHAST, EntityType.ELDER_GUARDIAN, EntityType.GIANT));

    public RushiaShield() {
        super("rushia_shield", Material.SHIELD);
        this.addVariable("mobs", data -> {
            String s = data.get(Keys.getKeys().RUSHIA_SHIELD_MOBS, PersistentDataType.STRING); //Pull data from item
            if (s != null) { //If it DOES exist
                s = s.replace(";", ", ").replace("_", " "); //Change PIG;ZOMBIE_PIGLIN into PIG, ZOMBIE PIGLIN
            }
            if (s == null || s.equals("")) s = "(None)"; //Put none if nothing exists
            return s;
        });
        this.setDisplayName(ChatColor.RED + "Rushia Shield")
                .addLore(ChatColor.GRAY + "Shield yourself with the power of the undead!")
                .addLore("")
                .addLore(ChatColor.GOLD + "Item Ability: Soul Recycling " + ChatColor.YELLOW + "RIGHT CLICK")
                .addLore(ChatColor.GRAY + "Stores " + ChatColor.YELLOW + "2 souls" + ChatColor.GRAY + " in the shield when you kill")
                .addLore(ChatColor.GRAY + "them while holding the shield. Use the shield")
                .addLore(ChatColor.GRAY + "to them summon the soul to defend you.")
                .addLore(ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.YELLOW + "10s")
                .addLore(ChatColor.DARK_GRAY + "Souls stored: " + ChatColor.YELLOW + "{mobs}");

        addProperty(Properties.RENAMABLE); //Allow it to be renamed
    }

    @Override
    public ItemStack buildStack(Player player) {
        ItemStack stack = super.buildStack(player);
        ItemMeta meta = stack.getItemMeta();

        //Add our custom data to built stacks
        meta.getPersistentDataContainer().set(Keys.getKeys().RUSHIA_SHIELD_MOBS, PersistentDataType.STRING, "");
        meta.getPersistentDataContainer().set(Keys.getKeys().RUSHIA_SHIELD_COUNT, PersistentDataType.INTEGER, 0);
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        stack.setItemMeta(meta);

        return updateStack(stack, player); //Fix the lore for our added things
    }

    public void killMob(Mob creature, Player player, ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();

        int count = meta.getPersistentDataContainer().get(Keys.getKeys().RUSHIA_SHIELD_COUNT, PersistentDataType.INTEGER);

        if (count > 2) return;

        String mobString = count == 0 ? creature.getType().name() :
                meta.getPersistentDataContainer().get(Keys.getKeys().RUSHIA_SHIELD_MOBS, PersistentDataType.STRING) + ";" + creature.getType().name();

        //Update the data on the item
        meta.getPersistentDataContainer().set(Keys.getKeys().RUSHIA_SHIELD_COUNT, PersistentDataType.INTEGER, count + 1);
        meta.getPersistentDataContainer().set(Keys.getKeys().RUSHIA_SHIELD_MOBS, PersistentDataType.STRING, mobString);

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

    @Override
    public boolean onInteract(Player player, CustomItem item, ItemStack stack) {
        new RushiaShieldAbility(player, stack, player.getInventory(), 0);
        return false;
    }

    @ItemEvent
    public void onTrigger(EventContext context, EntityDeathEvent event) {
        //If they are holding the item in the offhand or main hand
        if (context.getPosition() == EventContext.Position.OFFHAND || context.getPosition() == EventContext.Position.HELD) {
            LivingEntity entity = event.getEntity();

            //If the killer is the person holding this item and the entity is a mob but not a boss
            if (entity.getKiller() == context.getPlayer() && entity instanceof Mob && !(entity instanceof Boss)) {
                Player killer = entity.getKiller();

                //If the killed mob isn't a mob being used for a shield already & its a mob we are allowed to use
                if (!RushiaShieldAbility.getShieldMobs().contains(entity) && !RushiaShield.EXCEPTIONS.contains(entity.getType())) {
                    killMob((Mob) entity, entity.getKiller(), context.getStack()); //Run the killMob method
                    return;
                }

            }
        }
    }
}
