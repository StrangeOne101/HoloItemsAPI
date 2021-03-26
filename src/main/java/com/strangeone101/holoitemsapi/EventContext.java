package com.strangeone101.holoitemsapi;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EventContext {

    //Cache
    public static Map<Class<? extends Event>, Map<Player, Set<MutableTriple<CustomItem, ItemStack, Position>>>> CACHED_POSITIONS_BY_EVENT = new HashMap<>();
    public static Map<Player, Map<Integer, MutableTriple<CustomItem, ItemStack, Position>>> CACHED_POSITIONS_BY_SLOT = new HashMap<>();

    //Registry
    static Map<CustomItem, Map<Class<? extends Event>, Set<Method>>> ITEMEVENT_EXECUTORS = new HashMap<>();

    //Events that we have ItemEvent listeners for
    static Set<Class<? extends Event>> REGISTERED_EVENT_HANDLERS = new HashSet<>();

    public enum Position {
        HELD, OFFHAND, HOTBAR, ARMOR, INVENTORY, OTHER
    }

    private Player player;
    private CustomItem item;
    private ItemStack stack;
    private Position position;

    public EventContext(Player player, CustomItem item, ItemStack stack, Position position) {
        this.player = player;
        this.item = item;
        this.stack = stack;
        this.position = position;
    }

    public CustomItem getItem() {
        return item;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Position getPosition() {
        return position;
    }

    public Player getPlayer() {
        return player;
    }

    public static void fullCache(Player player) {
        CACHED_POSITIONS_BY_SLOT.put(player, new HashMap<>());

        for (Class<? extends Event> key : CACHED_POSITIONS_BY_EVENT.keySet()) {
            CACHED_POSITIONS_BY_EVENT.get(key).remove(player);
        }

        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);

            CustomItem item = CustomItemRegistry.getCustomItem(stack);

            if (ITEMEVENT_EXECUTORS.containsKey(item)) {
                Position pos = getPosition(slot, player.getInventory().getHeldItemSlot());
                MutableTriple<CustomItem, ItemStack, Position> triple = new MutableTriple<>(item, stack, pos);

                for (Class<? extends Event> clazz : ITEMEVENT_EXECUTORS.get(item).keySet()) {
                    if (!CACHED_POSITIONS_BY_EVENT.containsKey(clazz)) {
                        CACHED_POSITIONS_BY_EVENT.put(clazz, new HashMap<>());
                    }

                    if (!CACHED_POSITIONS_BY_EVENT.get(clazz).containsKey(player)) {
                        CACHED_POSITIONS_BY_EVENT.get(clazz).put(player, new HashSet<>());
                    }

                    CACHED_POSITIONS_BY_EVENT.get(clazz).get(player).add(triple);
                }
                CACHED_POSITIONS_BY_SLOT.get(player).put(slot, triple);
            }
        }
    }

    public static void release(Player player) {
        CACHED_POSITIONS_BY_SLOT.remove(player);

        for (Class key : CACHED_POSITIONS_BY_EVENT.keySet()) {
            CACHED_POSITIONS_BY_EVENT.get(key).remove(player);
        }
    }

    public static boolean isCached(Player player) {
        return CACHED_POSITIONS_BY_SLOT.containsKey(player);
    }

    public static Set<MutableTriple<CustomItem, ItemStack, Position>> getCache(Player player, Class<? extends Event> clazz) {
        if (!CACHED_POSITIONS_BY_EVENT.containsKey(clazz)) return new HashSet<>();
        if (!CACHED_POSITIONS_BY_EVENT.get(clazz).containsKey(player)) return new HashSet<>();

        return CACHED_POSITIONS_BY_EVENT.get(clazz).get(player);
    }

    public static void updateCacheSlot(Player player, int oldSlot, int newSlot) {
        Position pos = getPosition(newSlot, player.getInventory().getHeldItemSlot());

        if (CACHED_POSITIONS_BY_SLOT.containsKey(player) && CACHED_POSITIONS_BY_SLOT.get(player).containsKey(oldSlot)) {
            MutableTriple<CustomItem, ItemStack, Position> triple = CACHED_POSITIONS_BY_SLOT.get(player).get(oldSlot);
            triple.setRight(pos);
            CACHED_POSITIONS_BY_SLOT.get(player).remove(oldSlot);
            CACHED_POSITIONS_BY_SLOT.get(player).put(newSlot, triple);
        }
    }

    public static void cacheItem(Player player, int slot, ItemStack stack) {
        CustomItem item = CustomItemRegistry.getCustomItem(stack);
        if (item == null) return;

        if (!CACHED_POSITIONS_BY_SLOT.containsKey(player)) {
            CACHED_POSITIONS_BY_SLOT.put(player, new HashMap<>());
        }

        MutableTriple<CustomItem, ItemStack, Position> triple = new MutableTriple<>(item, stack, getPosition(slot, player.getInventory().getHeldItemSlot()));
        CACHED_POSITIONS_BY_SLOT.get(player).put(slot, triple);

        for (Class<? extends Event> clazz : ITEMEVENT_EXECUTORS.get(item).keySet()) {
            if (!CACHED_POSITIONS_BY_EVENT.containsKey(clazz)) {
                CACHED_POSITIONS_BY_EVENT.put(clazz, new HashMap<>());
            }

            if (!CACHED_POSITIONS_BY_EVENT.get(clazz).containsKey(player)) {
                CACHED_POSITIONS_BY_EVENT.get(clazz).put(player, new HashSet<>());
            }

            CACHED_POSITIONS_BY_EVENT.get(clazz).get(player).add(triple);
        }
    }

    public static void updateHeldSlot(Player player, int oldSlot, int newSlot) {
        if (CACHED_POSITIONS_BY_SLOT.containsKey(player)) {
            if (CACHED_POSITIONS_BY_SLOT.get(player).containsKey(oldSlot)) {
                CACHED_POSITIONS_BY_SLOT.get(player).get(oldSlot).setRight(getPosition(oldSlot, newSlot));
            }
            if (CACHED_POSITIONS_BY_SLOT.get(player).containsKey(newSlot)) {
                CACHED_POSITIONS_BY_SLOT.get(player).get(newSlot).setRight(getPosition(newSlot, newSlot));
            }
        }


    }

    public static boolean shouldCache(ItemStack stack) {
        return ITEMEVENT_EXECUTORS.containsKey(CustomItemRegistry.getCustomItem(stack));
    }

    public static void swapCacheSlots(Player player, int slot1, int slot2, int... heldItemSlot) {
        int heldSlot = heldItemSlot.length > 0 ? heldItemSlot[0] : player.getInventory().getHeldItemSlot();
        Position pos1 = getPosition(slot1, heldSlot);
        Position pos2 = getPosition(slot2, heldSlot);

        if (CACHED_POSITIONS_BY_SLOT.containsKey(player)) {
            MutableTriple<CustomItem, ItemStack, Position> triple1 = CACHED_POSITIONS_BY_SLOT.get(player).get(slot1);
            MutableTriple<CustomItem, ItemStack, Position> triple2 = CACHED_POSITIONS_BY_SLOT.get(player).get(slot2);

            if (triple1 != null) {
                triple1.setRight(pos2);
                CACHED_POSITIONS_BY_SLOT.get(player).put(slot2, triple1);

                //If we are swapping with null, remove the old position of this triple
                if (triple2 == null) {
                    CACHED_POSITIONS_BY_SLOT.get(player).remove(slot1);
                }
            }
            if (triple2 != null) {
                triple2.setRight(pos1);
                CACHED_POSITIONS_BY_SLOT.get(player).put(slot1, triple2);

                //If we are swapping with null, remove the old position of this triple
                if (triple1 == null) {
                    CACHED_POSITIONS_BY_SLOT.get(player).remove(slot2);
                }
            }
        }
    }

    /**
     * Uncache an item removed from the inventory
     * @param player The player
     * @param slot The slot we are uncaching
     */
    public static void uncacheSlot(Player player, int slot) {
        if (CACHED_POSITIONS_BY_SLOT.containsKey(player)) {
            Triple<CustomItem, ItemStack, Position> triple = CACHED_POSITIONS_BY_SLOT.get(player).get(slot);

            if (triple != null) {
                CACHED_POSITIONS_BY_SLOT.get(player).remove(slot); //Remove the slot

                if (CACHED_POSITIONS_BY_SLOT.get(player).isEmpty()) { //If they have no more cache left
                    CACHED_POSITIONS_BY_SLOT.remove(player); //Remove entire cache
                }

                for (Class<? extends Event> clazz : ITEMEVENT_EXECUTORS.get(triple.getLeft()).keySet()) {
                    if (CACHED_POSITIONS_BY_EVENT.containsKey(clazz) && CACHED_POSITIONS_BY_EVENT.get(clazz).containsKey(player)) {
                        CACHED_POSITIONS_BY_EVENT.get(clazz).get(player).remove(triple);
                    }
                }
            }
        }
    }

    private static Position getPosition(int slot, int heldItemSlot) {
        if (slot < 0) {
            return Position.OTHER;
        } else if (slot <= 8) {
            if (slot == heldItemSlot) {
                return Position.HELD;
            } else return Position.HOTBAR;
        } else if (slot >= 36 && slot <= 39) {
            return Position.ARMOR;
        } else if (slot == 40) {
            return Position.OFFHAND;
        }
        return Position.INVENTORY;
    }

    /**
     * Get active contexts
     * @param player The player
     * @return The active contexts
     */
    public static Collection<EventContext> getActive(Player player) {
        if (!CACHED_POSITIONS_BY_SLOT.containsKey(player)) return Collections.emptyList();

        List<EventContext> contexts = new ArrayList<>();
        for (Triple<CustomItem, ItemStack, Position> triple : CACHED_POSITIONS_BY_SLOT.get(player).values()) {
            contexts.add(new EventContext(player, triple.getLeft(), triple.getMiddle(), triple.getRight()));
        }
        return contexts;
    }

    static void triggerItemEvents(Event event) {
        if (CACHED_POSITIONS_BY_EVENT.containsKey(event.getClass())) {
            for (Player player : CACHED_POSITIONS_BY_EVENT.get(event.getClass()).keySet()) {
                for (Triple<CustomItem, ItemStack, Position> triple : CACHED_POSITIONS_BY_EVENT.get(event.getClass()).get(player)) {
                    EventContext context = new EventContext(player, triple.getLeft(), triple.getMiddle(), triple.getRight());

                    for (Method method : ITEMEVENT_EXECUTORS.get(context.getItem()).get(event.getClass())) {
                        try {
                            method.invoke(context.getItem(), context, event);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    }

    public static List<String> getRegistryDebug() {
        List<String> list = new ArrayList<>();

        for (CustomItem ci : ITEMEVENT_EXECUTORS.keySet()) {
            list.add("[" + ci.getInternalName() + "] " + String.join(", ",
                    ITEMEVENT_EXECUTORS.get(ci).keySet().stream().map(clazz -> clazz.getSimpleName())
                            .collect(Collectors.toList())));
        }
        return list;
    }
}
