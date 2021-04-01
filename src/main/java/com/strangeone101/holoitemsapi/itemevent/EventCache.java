package com.strangeone101.holoitemsapi.itemevent;

import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitemsapi.CustomItemRegistry;
import com.strangeone101.holoitemsapi.HoloItemsAPI;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EventCache {
    private static final boolean LOG_DEBUG = false;

    private static final Listener DUMMY_LISTENER = new Listener() {};

    //Cache
    @Deprecated
    public static Map<Class<? extends Event>, Map<Player, Set<MutableTriple<CustomItem, ItemStack, Position>>>> CACHED_POSITIONS_BY_EVENT = new HashMap<>();
    @Deprecated
    public static Map<Player, Map<Integer, MutableTriple<CustomItem, ItemStack, Position>>> CACHED_POSITIONS_BY_SLOT = new HashMap<>();

    public static Map<CustomItem, Map<Player, Map<Integer, Pair<ItemStack, Position>>>> POSITIONS_BY_ITEM = new HashMap<>();

    public static Set<Player> DIRTY_INVENTORY = new HashSet<>();

    //Registry
    static Map<CustomItem, Map<Class<? extends Event>, Set<Method>>> ITEMEVENT_EXECUTORS = new HashMap<>();

    static Map<Class<? extends Event>, Map<Method, MutableTriple<CustomItem, Target, ActiveConditions>>> METHODS_BY_EVENT = new HashMap<>();

    //Events that we have ItemEvent listeners for
    static Set<Class<? extends Event>> REGISTERED_EVENT_HANDLERS = new HashSet<>();

    public static void fullCache(Player player) {
        if (LOG_DEBUG) Bukkit.getLogger().info("Full cache begin on " + player);
        CACHED_POSITIONS_BY_SLOT.put(player, new HashMap<>());

        for (Class<? extends Event> key : CACHED_POSITIONS_BY_EVENT.keySet()) {
            CACHED_POSITIONS_BY_EVENT.get(key).remove(player);
        }

        //--
        for (Map.Entry<CustomItem, Map<Player, Map<Integer, Pair<ItemStack, Position>>>> entry : POSITIONS_BY_ITEM.entrySet()) {
            if (!ITEMEVENT_EXECUTORS.containsKey(entry.getKey())) continue;

            Map<Player, Map<Integer, Pair<ItemStack, Position>>> positionCache = entry.getValue();
            Map<Integer, Pair<ItemStack, Position>> playerSlotCache = positionCache.computeIfAbsent(player, k -> new HashMap<>());
            playerSlotCache.clear();
        }

        EventCache.DIRTY_INVENTORY.remove(player);
        //--

        PlayerInventory playerInventory = player.getInventory();
        for (int slot = 0; slot < playerInventory.getSize(); slot++) {
            ItemStack stack = playerInventory.getItem(slot);

            CustomItem item = CustomItemRegistry.getCustomItem(stack);
            if (item == null) continue;

            if (ITEMEVENT_EXECUTORS.containsKey(item)) {
                Position pos = getPosition(slot, playerInventory.getHeldItemSlot());
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

                //--
                Map<Integer, Pair<ItemStack, Position>> playerSlotCache = POSITIONS_BY_ITEM.get(item).get(player);
                playerSlotCache.put(slot, new MutablePair<>(stack, pos));
                if (LOG_DEBUG) Bukkit.getLogger().info("Cached " + stack.getAmount() + "x " + item.getInternalName() + " on slot " + slot + " @ " + pos.toString());
            } else {
                if (LOG_DEBUG) Bukkit.getLogger().info("Skipped check for item " + item.getInternalName());
            }
        }
        if (LOG_DEBUG) Bukkit.getLogger().info("Full cache end on " + player);
    }

    public static void release(Player player) {
        CACHED_POSITIONS_BY_SLOT.remove(player);

        for (Class key : CACHED_POSITIONS_BY_EVENT.keySet()) {
            CACHED_POSITIONS_BY_EVENT.get(key).remove(player);
        }

        for (Map<Player, Map<Integer, Pair<ItemStack, Position>>> positionCache : POSITIONS_BY_ITEM.values()) {
            positionCache.remove(player);
        }
    }

    public static boolean isCached(Player player) {
        for (Map<Player, Map<Integer, Pair<ItemStack, Position>>> positionCache : POSITIONS_BY_ITEM.values()) {
            if (positionCache.containsKey(player)) {
                if (LOG_DEBUG) Bukkit.getLogger().info("isCached " + player + " : true");
                return true;
            }
        }

        if (LOG_DEBUG) Bukkit.getLogger().info("isCached " + player + " : false");
        return false;
    }

    @Deprecated
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

        for (Map<Player, Map<Integer, Pair<ItemStack, Position>>> positionsCache : POSITIONS_BY_ITEM.values()) {
            Map<Integer, Pair<ItemStack, Position>> playerSlotCache = positionsCache.get(player);
            if (playerSlotCache == null) continue;
            Pair<ItemStack, Position> transfer = playerSlotCache.remove(oldSlot);
            if (transfer != null) {
                playerSlotCache.put(newSlot, transfer);
            }
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

        //---
        Map<Player, Map<Integer, Pair<ItemStack, Position>>> positionsCache = POSITIONS_BY_ITEM.get(item);
        Map<Integer, Pair<ItemStack, Position>> playerSlotCache = positionsCache.computeIfAbsent(player, k -> new HashMap<>());
        playerSlotCache.put(slot, new MutablePair<>(stack, getPosition(slot, player.getInventory().getHeldItemSlot())));
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
        //---
        for (Map<Player, Map<Integer, Pair<ItemStack, Position>>> positionsCache : POSITIONS_BY_ITEM.values()) {
            Map<Integer, Pair<ItemStack, Position>> playerSlotCache = positionsCache.get(player);
            if (playerSlotCache == null) continue;
            Pair<ItemStack, Position> transfer;
            transfer = playerSlotCache.get(oldSlot);
            if (transfer != null) {
                transfer.setValue(getPosition(oldSlot, newSlot));
            }
            transfer = playerSlotCache.get(newSlot);
            if (transfer != null) {
                transfer.setValue(getPosition(newSlot, newSlot));
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
        //--
        for (Map<Player, Map<Integer, Pair<ItemStack, Position>>> positionsCache : POSITIONS_BY_ITEM.values()) {
            Map<Integer, Pair<ItemStack, Position>> playerSlotCache = positionsCache.get(player);
            if (playerSlotCache == null) continue;

            Pair<ItemStack, Position> pair1 = playerSlotCache.get(slot1);
            Pair<ItemStack, Position> pair2 = playerSlotCache.get(slot2);

            if (pair1 != null) {
                pair1.setValue(pos2);
                playerSlotCache.put(slot2, pair1);
            }
            if (pair1 != null) {
                pair1.setValue(pos2);
                playerSlotCache.put(slot2, pair1);

                //If we are swapping with null, remove the old position of this triple
                if (pair2 == null) {
                    playerSlotCache.remove(slot1);
                }
            }
            if (pair2 != null) {
                pair2.setValue(pos1);
                playerSlotCache.put(slot1, pair2);

                //If we are swapping with null, remove the old position of this triple
                if (pair1 == null) {
                    playerSlotCache.remove(slot2);
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
        //--
        for (Map<Player, Map<Integer, Pair<ItemStack, Position>>> positionsCache : POSITIONS_BY_ITEM.values()) {
            Map<Integer, Pair<ItemStack, Position>> playerSlotCache = positionsCache.get(player);
            if (playerSlotCache == null) continue;
            playerSlotCache.remove(slot); //Remove the slot

            if (playerSlotCache.isEmpty()) { //If they have no more cache left
                positionsCache.remove(player); //Remove entire cache
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
        /*
        if (!CACHED_POSITIONS_BY_SLOT.containsKey(player)) return Collections.emptyList();

        List<EventContext> contexts = new ArrayList<>();
        for (Triple<CustomItem, ItemStack, Position> triple : CACHED_POSITIONS_BY_SLOT.get(player).values()) {
            contexts.add(new EventContext(player, triple.getLeft(), triple.getMiddle(), triple.getRight()));
        }
        return contexts; */

        List<EventContext> contexts = new ArrayList<>();
        for (Map.Entry<CustomItem, Map<Player, Map<Integer, Pair<ItemStack, Position>>>> positionsEntries : POSITIONS_BY_ITEM.entrySet()) {
            Map<Integer, Pair<ItemStack, Position>> playerSlotCache = positionsEntries.getValue().get(player);
            if (playerSlotCache == null) continue;

            CustomItem customItem = positionsEntries.getKey();

            for (Pair<ItemStack, Position> pairs : playerSlotCache.values()) {
                contexts.add(new EventContext(player, customItem, pairs.getLeft(), pairs.getRight()));
            }
        }
        return contexts;
    }

    static void triggerItemEvents(Event event) {
        if (!METHODS_BY_EVENT.containsKey(event.getClass())) return;

        for (Method m : METHODS_BY_EVENT.get(event.getClass()).keySet()) {
            Triple<CustomItem, Target, ActiveConditions> t = METHODS_BY_EVENT.get(event.getClass()).get(m);

            if (t.getRight() == ActiveConditions.NONE) { //We don't execute it based on whether the item is active
                EventContext context = new EventContext(null, t.getLeft(), null, null);
                try {
                    m.invoke(t.getLeft(), context, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else if (t.getMiddle() == Target.SELF) { //Execute only if the player is the one doing it
                if (event instanceof PlayerEvent) {
                    if (POSITIONS_BY_ITEM.get(t.getLeft()).containsKey(((PlayerEvent)event).getPlayer())) {
                        for (int slot : POSITIONS_BY_ITEM.get(t.getLeft()).get(((PlayerEvent)event).getPlayer()).keySet()) {
                            Pair<ItemStack, Position> pair = POSITIONS_BY_ITEM.get(t.getLeft()).get(((PlayerEvent)event).getPlayer()).get(slot);
                            if (t.getRight().matches(pair.getRight())) { //If activeConditions match Position
                                EventContext context = new EventContext(((PlayerEvent) event).getPlayer(), t.getLeft(), pair.getLeft(), pair.getRight());
                                try {
                                    m.invoke(t.getLeft(), context, event);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                }
            } else if (t.getMiddle() == Target.WORLD) { //Execute all events in the same world
                World world = null;
                if (event instanceof PlayerEvent) world = ((PlayerEvent)event).getPlayer().getWorld();
                else if (event instanceof BlockEvent) world = ((BlockEvent)event).getBlock().getWorld();
                else if (event instanceof EntityEvent) world = ((EntityEvent)event).getEntity().getWorld();
                else if (event instanceof InventoryEvent) world = ((InventoryEvent)event).getView().getPlayer().getWorld();
                if (world != null) {
                    for (Player player : POSITIONS_BY_ITEM.get(t.getLeft()).keySet()) {
                        if (player.getWorld() == world) { //If the world is the same
                            for (int slot : POSITIONS_BY_ITEM.get(t.getLeft()).get(player).keySet()) {
                                Pair<ItemStack, Position> pair = POSITIONS_BY_ITEM.get(t.getLeft()).get(player).get(slot);
                                if (t.getRight().matches(pair.getRight())) { //If activeConditions match Position
                                    EventContext context = new EventContext(player, t.getLeft(), pair.getLeft(), pair.getRight());
                                    try {
                                        m.invoke(t.getLeft(), context, event);
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            } else { //Events for all worlds and all players
                for (Player player : POSITIONS_BY_ITEM.get(t.getLeft()).keySet()) {
                    for (int slot : POSITIONS_BY_ITEM.get(t.getLeft()).get(player).keySet()) {
                        Pair<ItemStack, Position> pair = POSITIONS_BY_ITEM.get(t.getLeft()).get(player).get(slot);
                        if (t.getRight().matches(pair.getRight())) { //If activeConditions match Position
                            EventContext context = new EventContext(player, t.getLeft(), pair.getLeft(), pair.getRight());
                            try {
                                m.invoke(t.getLeft(), context, event);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        /*if (CACHED_POSITIONS_BY_EVENT.containsKey(event.getClass())) {
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
        }*/
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

    public static void registerEvents(CustomItem item) {
        HashSet<Method> methods = new HashSet<>();
        methods.addAll(Arrays.asList(item.getClass().getDeclaredMethods()));
        methods.addAll(Arrays.asList(item.getClass().getMethods()));
        for (Method method : methods) {
            if (method.isAnnotationPresent(ItemEvent.class)) {
                if (method.getParameterTypes().length < 1 || !method.getParameterTypes()[0].equals(EventContext.class) || !Event.class.isAssignableFrom(method.getParameterTypes()[1])) {
                    HoloItemsAPI.getPlugin().getLogger().severe("Item " + item.getInternalName()
                            + "attempted to register an invalid ItemEvent method signature \\" + method.toGenericString() + "\" in " + item.getClass());
                    continue;
                }

                Class<? extends Event> clazz = (Class<? extends Event>) method.getParameterTypes()[1];

                ItemEvent event = method.getDeclaredAnnotation(ItemEvent.class);

                if (!ITEMEVENT_EXECUTORS.containsKey(item)) {
                    ITEMEVENT_EXECUTORS.put(item, new HashMap<>());
                }

                if (!ITEMEVENT_EXECUTORS.get(item).containsKey(clazz)) {
                    ITEMEVENT_EXECUTORS.get(item).put(clazz, new HashSet<>());
                }

                if (!METHODS_BY_EVENT.containsKey(clazz)) {
                    METHODS_BY_EVENT.put(clazz, new HashMap<>());
                }

                method.setAccessible(true);
                ITEMEVENT_EXECUTORS.get(item).get(clazz).add(method);
                METHODS_BY_EVENT.get(clazz).put(method, new MutableTriple<>(item, event.target(), event.active()));

                //Make sure bukkit will trigger our event methods
                if (!REGISTERED_EVENT_HANDLERS.contains(clazz)) {
                    registerItemEventListener(clazz);
                }
            }
        }

        Map<Player, Map<Integer, Pair<ItemStack, Position>>> positionsCache = POSITIONS_BY_ITEM.get(item);
        if (positionsCache == null) {
            POSITIONS_BY_ITEM.put(item, new HashMap<>());
        } else {
            positionsCache.clear();
        }
    }

    private static void registerItemEventListener(Class<? extends Event> clazz) {
        EventExecutor executor = (listener, event) -> triggerItemEvents(event);

        Bukkit.getPluginManager().registerEvent(clazz, DUMMY_LISTENER, EventPriority.NORMAL, executor, HoloItemsAPI.getPlugin(), false);
        REGISTERED_EVENT_HANDLERS.add(clazz);
    }
}
