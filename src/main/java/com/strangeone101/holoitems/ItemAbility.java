package com.strangeone101.holoitems;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public abstract class ItemAbility {

    private static Map<Player, Map<Class<? extends ItemAbility>, ItemAbility>> INSTANCES = new HashMap<>();

    private Player player;
    private ItemStack stack;
    private Inventory inventory;
    private int slot;

    public ItemAbility(Player player, ItemStack stack, Inventory inventory, int slot) {
        this.player = player;
        this.stack = stack;
        this.inventory = inventory;
        this.slot = slot;
    }

    /**
     * Make this ability start calling {@link #tick()} every tick until {@link #remove()} is called
     */
    public void start() {
        if (!INSTANCES.containsKey(player)) {
            INSTANCES.put(player, new HashMap<>());
        }

        if (!INSTANCES.get(player).containsKey(this.getClass())) {
            INSTANCES.get(player).put(this.getClass(), this);
        }
    }

    /**
     * Called every tick until {@link #remove()} is called
     */
    public abstract void tick();

    /** Stop this ability from ticking anymore */
    public void remove() {
        if (INSTANCES.containsKey(player) && INSTANCES.get(player).containsKey(this.getClass())) {
            INSTANCES.get(player).remove(this.getClass());
            if (INSTANCES.get(player).isEmpty()) INSTANCES.remove(player);
        }
    }

    /**
     * Tick all abilities. Should only be called in the runnable
     */
    protected static void tickAll() {
        for (Player player : INSTANCES.keySet()) {
            for (ItemAbility ability : INSTANCES.get(player).values()) {
                ability.tick();
            }
        }
    }

    /**
     * Stops all abilities. Should be called on plugin disable.
     */
    protected static void removeAll() {
        for (Player player : INSTANCES.keySet()) {
            for (ItemAbility ability : INSTANCES.get(player).values()) {
                ability.remove();
            }
        }
    }

    /**
     * Whether an ability is already active
     * @param player The player
     * @param clazz The ability class
     * @return
     */
    public static boolean isAbilityActive(Player player, Class<? extends ItemAbility> clazz) {
        return INSTANCES.containsKey(player) && INSTANCES.get(player).containsKey(clazz);
    }

    /**
     * Get an existing ability
     * @param player The player
     * @param clazz The ability class
     * @return The ability, or null
     */
    public static <T extends ItemAbility> T getAbility(Player player, Class<T> clazz) {
        if (isAbilityActive(player, clazz)) {
            return (T) INSTANCES.get(player).get(clazz);
        }
        return null;
    }

    /**
     * How long the cooldown should be. In milliseconds.
     * @return The cooldown time
     */
    public abstract long getCooldownLength();

    /**
     * The item this ability should be used with
     * @return
     */
    public abstract CustomItem getItem();

    /**
     * Check whether the item is currently on cooldown
     * @return
     */
    public boolean isOnCooldown() {
        return stack != null && stack.hasItemMeta() &&
                stack.getItemMeta().getPersistentDataContainer().has(HoloItemsPlugin.getKeys().CUSTOM_ITEM_COOLDOWN, PersistentDataType.LONG) &&
                stack.getItemMeta().getPersistentDataContainer().get(HoloItemsPlugin.getKeys().CUSTOM_ITEM_COOLDOWN, PersistentDataType.LONG) > System.currentTimeMillis();
    }

    /**
     * Gets how long in milliseconds until the cooldown is up.
     * @return
     */
    public long getCooldownRemaining() {
        if (stack == null || !stack.hasItemMeta() || !stack.getItemMeta().getPersistentDataContainer().has(HoloItemsPlugin.getKeys().CUSTOM_ITEM_COOLDOWN, PersistentDataType.LONG)) return -1;
        return Math.max(0, stack.getItemMeta().getPersistentDataContainer().get(HoloItemsPlugin.getKeys().CUSTOM_ITEM_COOLDOWN, PersistentDataType.LONG) - System.currentTimeMillis());

    }

    public void addCooldown() {
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(HoloItemsPlugin.getKeys().CUSTOM_ITEM_COOLDOWN, PersistentDataType.LONG, System.currentTimeMillis() + getCooldownLength());
        stack.setItemMeta(meta);
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getSlot() {
        return slot;
    }

    public static class CustomItemAbilityTask extends BukkitRunnable {
        @Override
        public void run() {
            tickAll();
        }
    }

}
