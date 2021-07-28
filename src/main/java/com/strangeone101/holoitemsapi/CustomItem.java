package com.strangeone101.holoitemsapi;

import com.strangeone101.holoitemsapi.util.ItemUtils;
import com.strangeone101.holoitemsapi.util.ReflectionUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A class for creating custom items. Be sure to call {@link #register()} after creating it
 * to properly register it
 */
public class CustomItem {

    private String name;
    private int internalIntID;

    private Material material;
    private Material fakeMaterial;
    private String displayName;
    private List<String> lore = new ArrayList<>();
    private boolean jsonLore = false;
    private int maxDurability = 0;
    private boolean stackable = true;
    private Set<Property> properties = new HashSet<>();
    private String extraData;
    private Random random;
    private boolean ench;
    private BiConsumer<ItemStack, ItemMeta> onBuild;
    private BiConsumer<ItemStack, ItemMeta> onUpdate;

    private Map<Attribute, Pair<AttributeModifier.Operation, Double>> attributes = new HashMap<>();
    private Map<String, Function<PersistentDataContainer, String>> variables = new HashMap<>();

    private CustomItem(String name) {
        this.name = name.toLowerCase();
    }

    public CustomItem(String name, Material material) {
        this(name);
        this.material = material;
        this.random = new Random(name.hashCode());
    }

    public CustomItem(String name, Material material, String displayName) {
        this(name, material);
        this.displayName = displayName;
    }

    public CustomItem(String name, Material material, String displayName, List<String> lore) {
        this(name, material, displayName);
        this.lore = lore;
    }

    /**
     * Gets the internal name of this custom item
     * @return The internal name
     */
    public final String getInternalName() {
        return name;
    }

    /**
     * Create a new ItemStack for use. NOT for updating existing ones; see {@link #updateStack(ItemStack, Player)}
     * @param player The player
     * @return The ItemStack
     */
    public ItemStack buildStack(Player player) {
        ItemStack stack = new ItemStack(getMaterial());
        this.random = new Random(name.hashCode());
        ItemMeta meta = stack.getItemMeta();

        //It's important to use the functions `getDisplayName()` and `getLore()` bellow
        //instead of the field names in case an object overrides them
        meta.setDisplayName(replaceVariables(getDisplayName(), meta.getPersistentDataContainer()));
        List<String> lore = new ArrayList<>();

        for (String line : getLore()) {
            lore.add(replaceVariables(line, meta.getPersistentDataContainer()));
        }
        if (!this.jsonLore) {
            meta.setLore(lore);
        }
        if (internalIntID != 0) meta.setCustomModelData(internalIntID); //Used for resource packs

        if (meta instanceof SkullMeta) {
            if (extraData != null) {
                ItemUtils.setSkin((SkullMeta) meta, extraData);
            }
        }

        if (player != null) {
            if (properties.contains(Properties.OWNER)) {
                Properties.OWNER.set(meta.getPersistentDataContainer(), player.getUniqueId());
                Properties.OWNER_NAME.set(meta.getPersistentDataContainer(), player.getName());
            }
        }
        if (properties.contains(Properties.COOLDOWN)) {
            Properties.COOLDOWN.set(meta.getPersistentDataContainer(), 0L);
        }

        Properties.ITEM_ID.set(meta.getPersistentDataContainer(), getInternalName());
        //meta.getPersistentDataContainer().set(HoloItemsPlugin.getKeys().CUSTOM_ITEM_ID, PersistentDataType.STRING, getInternalName());
        if (getMaxDurability() > 0) {
            meta.getPersistentDataContainer().set(HoloItemsAPI.getKeys().CUSTOM_ITEM_DURABILITY, PersistentDataType.INTEGER, 0);
        }

         //If the item shouldn't be stackable, add a random INTEGER to the NBT
        Properties.UNSTACKABLE.set(meta.getPersistentDataContainer(), !isStackable());

        if (ench) {
            stack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        stack.setItemMeta(meta);

        //Add all attributes to the item
        for (Attribute attr : getAttributes().keySet()) {
            Pair<AttributeModifier.Operation, Double> pair = getAttributes().get(attr);
            ItemUtils.setAttriute(pair.getRight(), attr, pair.getLeft(), stack);
        }



        if (this.jsonLore) {
            ReflectionUtils.setTrueLore(stack, lore);
        }

        if (onBuild != null) {
            meta = stack.getItemMeta();
            onBuild.accept(stack, meta);
        }

        return stack;
    }

    /**
     * Updates an existing itemstack with updated lore, name and variables
     * @param stack The itemstack
     * @param player The player holding it
     * @return The updated stack
     */
    public ItemStack updateStack(ItemStack stack, Player player) {
        ItemMeta originalMeta = stack.getItemMeta();
        ItemMeta meta = originalMeta;

        if (getMaterial() != stack.getType()) {
            if (originalMeta instanceof Damageable) {
                int damage = ((Damageable)originalMeta).getDamage();
                stack = buildStack(player); //Rebuild from scratch
                meta = stack.getItemMeta();
                if (meta instanceof Damageable) {
                    ((Damageable) meta).setDamage(damage);
                }
            }
        }
        if (properties.contains(Properties.OWNER)) {
            UUID uuid = Properties.OWNER.get(meta.getPersistentDataContainer());
            String ownerName;
            if (uuid != null) { //The owner can still be none if this is built using no player
                if (Bukkit.getPlayer(uuid) != null) { //If the player is online, use the new name
                    ownerName = Bukkit.getPlayer(uuid).getName();
                } else if (Properties.OWNER_NAME.has(meta.getPersistentDataContainer())) {
                    ownerName = Properties.OWNER_NAME.get(meta.getPersistentDataContainer());
                } else ownerName = player.getName(); //Failsafe is the new player's name
                Properties.OWNER_NAME.set(meta.getPersistentDataContainer(), ownerName);
            } else { //Owner is not defined but it should be
                if (player != null) { //Be sure we aren't gonna get an NPE
                    Properties.OWNER.set(meta.getPersistentDataContainer(), player.getUniqueId());
                    Properties.OWNER_NAME.set(meta.getPersistentDataContainer(), player.getName());
                }
            }
        }

        if (!properties.contains(Properties.RENAMABLE) || Properties.RENAMABLE.get(meta.getPersistentDataContainer()) == 0) {
            //It's important to use the functions `getDisplayName()` and `getLore()` bellow
            //instead of the field names in case an object overrides them
            meta.setDisplayName(replaceVariables(getDisplayName(), meta.getPersistentDataContainer()));
        }


        List<String> lore = new ArrayList<>();

        for (String line : getLore()) {
            lore.add(replaceVariables(line, meta.getPersistentDataContainer()));
        }
        if (!this.jsonLore) {
            meta.setLore(lore);
        }
        if (internalIntID != 0) meta.setCustomModelData(internalIntID); //Used for resource packs
        if (meta instanceof SkullMeta) {
            if (extraData != null) {
                ItemUtils.setSkin((SkullMeta) meta, extraData);
            }
        }

        if (ench) {
            stack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        stack.setItemMeta(meta);

        //Add all attributes to the item
        for (Attribute attr : getAttributes().keySet()) {
            Pair<AttributeModifier.Operation, Double> pair = getAttributes().get(attr);
            ItemUtils.setAttriute(pair.getRight(), attr, pair.getLeft(), stack);
        }

        if (this.jsonLore) {
            ReflectionUtils.setTrueLore(stack, lore);
        }

        if (onUpdate != null) {
            meta = stack.getItemMeta();
            onUpdate.accept(stack, meta);
        }

        return stack;
    }

    /**
     * Damages the item's durability
     * @param stack The itemstack
     * @param amount The amount to damage it
     * @param player The player damageing it
     */
    public void damageItem(ItemStack stack, int amount, Player player) {
        if (getMaxDurability() > 0 && player.getGameMode() != GameMode.CREATIVE) {
            ItemMeta meta = stack.getItemMeta();
            int damage = meta.getPersistentDataContainer().getOrDefault(HoloItemsAPI.getKeys().CUSTOM_ITEM_DURABILITY, PersistentDataType.INTEGER, 0);
            damage += amount;

            if (damage > getMaxDurability()) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                player.getWorld().spawnParticle(Particle.ITEM_CRACK, player.getLocation(), 16, stack.getData());
                stack.setType(Material.AIR);
                return;
            }

            meta.getPersistentDataContainer().set(HoloItemsAPI.getKeys().CUSTOM_ITEM_DURABILITY, PersistentDataType.INTEGER, damage);
            if (meta instanceof Damageable) {
                ((Damageable) meta).setDamage((damage / getMaxDurability()) * stack.getType().getMaxDurability());
            }
            stack.setItemMeta(meta);
        }
    }

    /**
     * Get a fancy string that represents the durability % left
     * @param durability The durability
     * @param maxDurability The max durability
     * @return The string
     */
    public static String getDurabilityString(int durability, int maxDurability) {
        if (maxDurability == 0) return ""; //No durability
        double percentage = durability / maxDurability;
        double bigPercentage = percentage * 100;
        ChatColor color = ChatColor.DARK_RED;
        if (bigPercentage >= 90) color = ChatColor.DARK_GREEN;
        else if (bigPercentage >= 60) color = ChatColor.GREEN;
        else if (bigPercentage >= 40) color = ChatColor.YELLOW;
        else if (bigPercentage >= 25) color = ChatColor.GOLD;
        else if (bigPercentage >= 5) color = ChatColor.RED;
        int percentInt = (int) (15 * percentage) + 1;
        //String template = "||||||xxxx||||||";
        String template = "||||||x||||||";
        String coloredPart, greyPart;

        boolean greyAfterPercent;
        if (percentInt <= 6) {
            coloredPart = color + template.substring(0, percentInt);
            greyPart = ChatColor.GRAY + template.substring(percentInt);
            greyAfterPercent = true;
        } else {
            coloredPart = color + template.substring(0, percentInt - 3);
            greyPart = ChatColor.GRAY + template.substring(percentInt - 3);
            greyAfterPercent = false;
        }

        String complete = coloredPart + greyPart;
        DecimalFormat dc = new DecimalFormat();
        dc.setMaximumFractionDigits(2);
        dc.setMaximumIntegerDigits(3);
        dc.setMinimumFractionDigits(0);
        dc.setMinimumIntegerDigits(2);

        complete = complete.replace("x", color + dc.format(bigPercentage) + "%" + (greyAfterPercent ? ChatColor.GRAY : color));

        return complete;

    }

    /**
     * Get the durability on this custom item
     * @param stack The custom item stack
     * @return The durability
     */
    public int getDurability(ItemStack stack) {
        if (getMaxDurability() > 0) {
            ItemMeta meta = stack.getItemMeta();
            return meta.getPersistentDataContainer().getOrDefault(HoloItemsAPI.getKeys().CUSTOM_ITEM_DURABILITY, PersistentDataType.INTEGER, 0);
        }
        return 0;
    }

    /**
     * Set the durability of this custom item
     * @param stack The custom item stack
     * @param durability The durability
     */
    public void setDurability(ItemStack stack, int durability) {
        if (getMaxDurability() > 0) {
            if (durability <= 0) {
                stack.setType(Material.AIR);
                return;
            }
            ItemMeta meta = stack.getItemMeta();
            meta.getPersistentDataContainer().set(HoloItemsAPI.getKeys().CUSTOM_ITEM_DURABILITY, PersistentDataType.INTEGER, durability);

            if (meta instanceof Damageable) {
                ((Damageable) meta).setDamage((durability / getMaxDurability()) * stack.getType().getMaxDurability());
            }

            stack.setItemMeta(meta); //Update item
        }
        return;
    }

    /**
     * Set the custom skin of the head
     * @param skin The skin
     * @return Itself
     */
    public CustomItem setHeadSkin(String skin) {
        if (material != Material.PLAYER_HEAD && material != Material.PLAYER_WALL_HEAD) {
            this.extraData = skin;
        }

        return this;
    }

    /**
     * Replaces the string provided with variables
     * @param string The string
     * @param dataHolder The data holder
     * @return The replaced string
     */
    public String replaceVariables(String string, PersistentDataContainer dataHolder) {
        String s = string;
        if (getMaxDurability() > 0) {
            int damage = dataHolder.getOrDefault(HoloItemsAPI.getKeys().CUSTOM_ITEM_DURABILITY, PersistentDataType.INTEGER, 0);
            damage = getMaxDurability() - damage;
            s = s.replace("{durability}", getDurabilityString(damage, getMaxDurability()));
        }
        for (String variable : variables.keySet()) {
            String endResult = variables.get(variable).apply(dataHolder);

            if (endResult == null) continue; //Variable not ready for use yet

            s = s.replace("{" + variable + "}", endResult);
        }
        return s;
    }

    public void addVariable(String variable, Function<PersistentDataContainer, String> function) {
        variables.put(variable, function);
    }

    /**
     * Unimplemented
     * @param speedPercentage
     * @return Itself
     */
    @Deprecated
    public CustomItem setToolSpeed(double speedPercentage) {
        //TODO
        return this;
    }

    /**
     * Set the display name
     * @param displayName The display name
     * @return Itself
     */
    public CustomItem setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Get the custom display name
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the material
     * @return The material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Set the material
     * @param material The material
     * @return Itself
     */
    public CustomItem setMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Make the item have an enchanted glow
     * @param glow True to glow
     * @return Itself
     */
    public CustomItem setEnchantedGlow(boolean glow) {
        this.ench = true;
        return this;
    }

    /**
     * Whether the item has an enchanted glow
     * @return The glow state
     */
    public boolean hasEnchantedGlow() {
        return this.ench;
    }

    /**
     * Get the lore
     * @return The lore
     */
    public List<String> getLore() {
        return lore;
    }

    /**
     * Set the lore
     * @param lore The lore
     * @return Itself
     */
    public CustomItem setLore(List<String> lore) {
        this.lore = lore;
        this.jsonLore = false;
        return this;
    }

    /**
     * Add a line to the lore
     * @param string The line to add
     * @return Itself
     */
    public CustomItem addLore(String string) {
        if (this.lore == null) this.lore = new ArrayList<>();

        if (this.jsonLore) {
            //Don't bother adding white and non italics to nothing
            if (string.length() == 0) lore.add(ComponentSerializer.toString(new TextComponent()));
            else {
                //Append white and non italics to the front so it doesn't appear in italics
                BaseComponent comp = new ComponentBuilder().append("")
                        .color(net.md_5.bungee.api.ChatColor.WHITE).italic(false).getCurrentComponent();
                comp.setExtra(Arrays.asList(TextComponent.fromLegacyText(string)));
                lore.add(ComponentSerializer.toString(comp));
            }

        } else {
            lore.add(string);
        }

        return this;
    }

    public CustomItem addLore(BaseComponent component) {
        if (this.lore == null) this.lore = new ArrayList<>();
        if (!this.jsonLore) {
            this.jsonLore = true;
            List<String> jsonList = new ArrayList<>();

            for (String s : this.lore) {
                //Don't bother adding white and non italics to nothing
                if (s.length() == 0) jsonList.add(ComponentSerializer.toString(new TextComponent()));
                else {
                    //Append white and non italics to the front so it doesn't appear in italics
                    BaseComponent comp = new ComponentBuilder().append("")
                            .color(net.md_5.bungee.api.ChatColor.WHITE).italic(false).getCurrentComponent();
                    comp.setExtra(Arrays.asList(TextComponent.fromLegacyText(s)));
                    jsonList.add(ComponentSerializer.toString(comp));
                }
            }
            this.lore = jsonList;
        }

        BaseComponent baseComp = new ComponentBuilder().append("")
                .color(net.md_5.bungee.api.ChatColor.WHITE).italic(false).getCurrentComponent();
        baseComp.addExtra(component);
        this.lore.add(ComponentSerializer.toString(baseComp));
        return this;
    }

    /**
     * Get the max durability of the item
     * @return The durability
     */
    public int getMaxDurability() {
        return maxDurability;
    }

    /**
     * Set the max durability of the item
     * @param maxDurability The durability
     * @return Itself
     */
    public CustomItem setMaxDurability(int maxDurability) {
        this.maxDurability = maxDurability;
        return this;
    }

    public CustomItem setInternalID(int id) {
        this.internalIntID = id;
        return this;
    }

    public int getInternalID() {
        return internalIntID;
    }

    /**
     * Register this item to the registry
     * @return Itself
     */
    public CustomItem register() {
        CustomItemRegistry.register(this);
        return this;
    }

    /**
     * If the item is stackable
     * @return True if stackable
     */
    public boolean isStackable() {
        return stackable;
    }

    /**
     * Whether the item can be stacked
     * @param stackable Stackable
     * @return Itself
     */
    public CustomItem setStackable(boolean stackable) {
        this.stackable = stackable;
        return this;
    }

    /**
     * Get the properties of this item
     * @return The properties
     */
    public Set<Property> getProperties() {
        return properties;
    }

    /**
     * Add a property to the item
     * @param property The property
     * @return Itself
     */
    public CustomItem addProperty(Property property) {
        this.properties.add(property);
        return this;
    }

    @Override
    public String toString() {
        return "CustomItem{" +
                "name='" + name + '\'' +
                ", textureID=" + internalIntID +
                ", material=" + material +
                ", displayName='" + displayName + "\'\u00A7r'" +
                ", maxDurability=" + maxDurability +
                ", stackable=" + stackable +
                ", properties=" + properties +
                ", extraData='" + extraData + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomItem that = (CustomItem) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public CustomItem setDamage(double damage) {
        getAttributes().put(Attribute.GENERIC_ATTACK_DAMAGE,
                new ImmutablePair<>(AttributeModifier.Operation.ADD_NUMBER, damage));

        return this;
    }

    public CustomItem setDamagePercentage(double percentage) {
        getAttributes().put(Attribute.GENERIC_ATTACK_DAMAGE,
                new ImmutablePair<>(AttributeModifier.Operation.ADD_SCALAR, percentage));

        return this;
    }

    public CustomItem setArmor(int armor) {
        getAttributes().put(Attribute.GENERIC_ARMOR,
                new ImmutablePair<>(AttributeModifier.Operation.ADD_NUMBER, (double)armor));

        return this;
    }

    public CustomItem setArmorToughness(int armor) {
        getAttributes().put(Attribute.GENERIC_ARMOR_TOUGHNESS,
                new ImmutablePair<>(AttributeModifier.Operation.ADD_NUMBER, (double)armor));

        return this;
    }

    public CustomItem setHealth(int health) {
        getAttributes().put(Attribute.GENERIC_MAX_HEALTH,
                new ImmutablePair<>(AttributeModifier.Operation.ADD_NUMBER, (double)health));

        return this;
    }

    public CustomItem setHealthPercentage(double percentage) {
        getAttributes().put(Attribute.GENERIC_MAX_HEALTH,
                new ImmutablePair<>(AttributeModifier.Operation.ADD_SCALAR, (double)percentage));

        return this;
    }

    public CustomItem setSpeed(double percentage) {
        getAttributes().put(Attribute.GENERIC_MOVEMENT_SPEED,
                new ImmutablePair<>(AttributeModifier.Operation.ADD_SCALAR, percentage));

        return this;
    }

    public CustomItem setKnockbackResistance(double percentage) {
        getAttributes().put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                new ImmutablePair<>(AttributeModifier.Operation.ADD_SCALAR, percentage));

        return this;
    }

    public CustomItem setAttackSpeed(double percentage) {
        getAttributes().put(Attribute.GENERIC_ATTACK_SPEED,
                new ImmutablePair<>(AttributeModifier.Operation.ADD_SCALAR, percentage));

        return this;
    }

    public CustomItem setAttackKnockback(double percentage) {
        getAttributes().put(Attribute.GENERIC_ATTACK_KNOCKBACK,
                new ImmutablePair<>(AttributeModifier.Operation.ADD_SCALAR, percentage));

        return this;
    }

    public CustomItem setLuck(double percentage) {
        getAttributes().put(Attribute.GENERIC_LUCK,
                new ImmutablePair<>(AttributeModifier.Operation.ADD_SCALAR, percentage));

        return this;
    }

    public Map<Attribute, Pair<AttributeModifier.Operation, Double>> getAttributes() {
        return attributes;
    }

    public CustomItem setVisibleMaterial(Material material) {
        this.fakeMaterial = material;
        return this;
    }

    /**
     * Run some code when this item is built. Used in case you don't want to create
     * a new class just to change something about the item.
     *
     * {code}item.onBuild((itemstack, meta) -> itemstack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10)){code}
     * @param consumer The code to run
     * @return Itself
     */
    public CustomItem onBuild(BiConsumer<ItemStack, ItemMeta> consumer) {
        this.onBuild = consumer;
        return this;
    }

    /**
     * Run some code when this item is updated (picked up or regenerated). Used in case you don't want to create
     * a new class just to change something about the item.
     *
     * {code}item.onUpdate((itemstack, meta) -> itemstack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10)){code}
     * @param consumer The code to run
     * @return Itself
     */
    public CustomItem onUpdate(BiConsumer<ItemStack, ItemMeta> consumer) {
        this.onUpdate = consumer;
        return this;
    }
}
