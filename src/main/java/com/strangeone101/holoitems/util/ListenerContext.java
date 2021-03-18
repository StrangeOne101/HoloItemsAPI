package com.strangeone101.holoitems.util;

import com.strangeone101.holoitems.CustomItem;
import com.strangeone101.holoitems.CustomItemRegistry;
import com.strangeone101.holoitems.items.interfaces.BehaviourListener;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ListenerContext<E extends Event> {

    public static Map<Class<? extends Event>, Map<Player, Set<MutableTriple<CustomItem, ItemStack, Position>>>> CACHED_POSITIONS_BY_EVENT = new HashMap<>();
    public static Map<Player, Map<Integer, MutableTriple<CustomItem, ItemStack, Position>>> CACHED_POSITIONS_BY_SLOT = new HashMap<>();

    public static enum Position {
        HELD, OFFHAND, HOTBAR, ARMOR, INVENTORY
    }

    private Player player;
    private CustomItem item;
    private ItemStack stack;
    private E event;
    private Position position;

    public ListenerContext(Player player, CustomItem item, ItemStack stack, E entity, Position position) {
        this.player = player;
        this.item = item;
        this.stack = stack;
        this.event = entity;
        this.position = position;
    }

    public CustomItem getItem() {
        return item;
    }

    public ItemStack getStack() {
        return stack;
    }

    public E getEvent() {
        return event;
    }

    public Position getPosition() {
        return position;
    }

    public Player getPlayer() {
        return player;
    }

    public static void fullCache(Player player) {
        CACHED_POSITIONS_BY_SLOT.put(player, new HashMap<>());

        for (Class key : CACHED_POSITIONS_BY_EVENT.keySet()) {
            CACHED_POSITIONS_BY_EVENT.get(key).remove(player);
        }

        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);

            CustomItem item = CustomItemRegistry.getCustomItem(stack);

            if (item instanceof BehaviourListener) {
                Position pos = Position.INVENTORY;
                if (slot >= 0 && slot <= 8) {
                    if (slot == player.getInventory().getHeldItemSlot()) {
                        pos = Position.HELD;
                    } else pos = Position.HOTBAR;
                } else if (slot >= 100 && slot <= 103) {
                    pos = Position.ARMOR;
                } else if (slot == -146) {
                    pos = Position.OFFHAND;
                }

                Class<? extends Event> clazz = ((BehaviourListener) item).getEventClass();

                if (!CACHED_POSITIONS_BY_EVENT.containsKey(clazz)) {
                    CACHED_POSITIONS_BY_EVENT.put(clazz, new HashMap<>());
                }

                if (!CACHED_POSITIONS_BY_EVENT.get(clazz).containsKey(player)) {
                    CACHED_POSITIONS_BY_EVENT.get(clazz).put(player, new HashSet<>());
                }

                MutableTriple<CustomItem, ItemStack, Position> triple = new MutableTriple<>(item, stack, pos);

                CACHED_POSITIONS_BY_EVENT.get(clazz).get(player).add(triple);
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

        return CACHED_POSITIONS_BY_EVENT.get(player).get(clazz);
    }

    public static void updateCacheSlot(Player player, int oldSlot, int newSlot) {
        Position pos = getPosition(newSlot, player.getInventory().getHeldItemSlot());

        if (CACHED_POSITIONS_BY_SLOT.containsKey(player)) {
            MutableTriple<CustomItem, ItemStack, Position> triple = CACHED_POSITIONS_BY_SLOT.get(player).get(oldSlot);
            triple.setRight(pos);
            CACHED_POSITIONS_BY_SLOT.get(player).remove(oldSlot);
            CACHED_POSITIONS_BY_SLOT.get(player).put(newSlot, triple);
        }
    }

    public static void swapCacheSlots(Player player, int slot1, int slot2) {
        Position pos1 = getPosition(slot1, player.getInventory().getHeldItemSlot());
        Position pos2 = getPosition(slot2, player.getInventory().getHeldItemSlot());

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

    private static Position getPosition(int slot, int heldItemSlot) {
        Position pos = Position.INVENTORY;
        if (slot >= 0 && slot <= 8) {
            if (slot == heldItemSlot) {
                pos = Position.HELD;
            } else pos = Position.HOTBAR;
        } else if (slot >= 100 && slot <= 103) {
            pos = Position.ARMOR;
        } else if (slot == -146) {
            pos = Position.OFFHAND;
        }
        return pos;
    }
}
