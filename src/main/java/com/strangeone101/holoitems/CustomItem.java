package com.strangeone101.holoitems;

import com.strangeone101.holoitems.util.UUIDTagType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public abstract class CustomItem {

    private String name;
    private int internalIntID;

    private Material material;
    private String displayName;
    private List<String> lore = new ArrayList<>();

    private Map<String, Function<PersistentDataContainer, String>> variables = new HashMap<>();

    private CustomItem(String name) {
        this.name = name.toLowerCase();
    }

    public CustomItem(String name, Material material) {
        this(name);
        this.material = material;
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
     * @return The ItemStack
     */
    public ItemStack buildStack(Player player) {
        ItemStack stack = new ItemStack(getMaterial());

        ItemMeta meta = stack.getItemMeta();

        //It's important to use the functions `getDisplayName()` and `getLore()` bellow
        //instead of the field names in case an object overrides them
        meta.setDisplayName(replaceVariables(getDisplayName(), player, player == null ? "Player" : player.getName(), meta.getPersistentDataContainer()));
        List<String> lore = new ArrayList<>();

        for (String line : getLore()) {
            lore.add(replaceVariables(line, player, player == null ? "Player" : player.getName(), meta.getPersistentDataContainer()));
        }
        meta.setLore(lore);
        meta.setCustomModelData(internalIntID); //Used for resource packs

        if (player != null) {
            meta.getPersistentDataContainer().set(HoloItemsPlugin.getKeys().CUSTOM_ITEM_OWNER, UUIDTagType.TYPE, player.getUniqueId());
        }
        meta.getPersistentDataContainer().set(HoloItemsPlugin.getKeys().CUSTOM_ITEM_OWNER_NAME, PersistentDataType.STRING, player == null ? "Player" : player.getName());
        meta.getPersistentDataContainer().set(HoloItemsPlugin.getKeys().CUSTOM_ITEM_COOLDOWN, PersistentDataType.LONG, 0L);
        meta.getPersistentDataContainer().set(HoloItemsPlugin.getKeys().CUSTOM_ITEM_ID, PersistentDataType.STRING, getInternalName());

        stack.setItemMeta(meta);

        return stack;
    }

    /**
     * Updates an existing itemstack with updated lore, name and variables
     * @param stack The itemstack
     * @param player The player holding it
     * @return
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
        UUID owner = null;
        //If they have the owner key, extract it
        if (originalMeta.getPersistentDataContainer().has(HoloItemsPlugin.getKeys().CUSTOM_ITEM_OWNER, UUIDTagType.TYPE)) {
            owner = originalMeta.getPersistentDataContainer().get(HoloItemsPlugin.getKeys().CUSTOM_ITEM_OWNER, UUIDTagType.TYPE);
        } else if (player != null) owner = player.getUniqueId();

        String ownerName = "Player";

        if (owner != null) { //The owner can still be none if this is built using no player
            if (Bukkit.getPlayer(owner) != null) { //If the player is online, use the new name
                ownerName = Bukkit.getPlayer(owner).getName();
            //If they have the owner name key, extract it
            } else if (originalMeta.getPersistentDataContainer().has(HoloItemsPlugin.getKeys().CUSTOM_ITEM_OWNER_NAME, PersistentDataType.STRING)) {
                ownerName = originalMeta.getPersistentDataContainer().get(HoloItemsPlugin.getKeys().CUSTOM_ITEM_OWNER_NAME, PersistentDataType.STRING);
            } else ownerName = player.getName(); //Failsafe is the new player's name
        }

        //It's important to use the functions `getDisplayName()` and `getLore()` bellow
        //instead of the field names in case an object overrides them
        meta.setDisplayName(replaceVariables(getDisplayName(), player, ownerName, meta.getPersistentDataContainer()));
        List<String> lore = new ArrayList<>();

        for (String line : getLore()) {
            lore.add(replaceVariables(line, player, ownerName, meta.getPersistentDataContainer()));
        }
        meta.setLore(lore);
        meta.setCustomModelData(internalIntID); //Used for resource packs

        meta.getPersistentDataContainer().set(HoloItemsPlugin.getKeys().CUSTOM_ITEM_OWNER_NAME, PersistentDataType.STRING, ownerName); //Update owner name

        stack.setItemMeta(meta);

        return stack;
    }

    public String replaceVariables(String string, Player holder, String owner, PersistentDataContainer dataHolder) {
        String s = string.replace("{player}", owner)
                .replace("{name}", owner)
                .replace("{owner}", owner);
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
        return this;
    }

    /**
     * Add a line to the lore
     * @param string The line to add
     * @return Itself
     */
    public CustomItem addLore(String string) {
        if (this.lore == null) this.lore = new ArrayList<>();

        lore.add(string);
        return this;
    }

    protected void setInternalIntegerID(int id) {
        this.internalIntID = id;
    }

    public void selfRegister() {
        CustomItemRegistry.register(this);
    }
}
