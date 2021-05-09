package com.strangeone101.holoitemsapi.tileentity;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class CustomTileEntity {

    private String name;

    private boolean inputEnabled;
    private boolean outputEnabled;

    private Map<Integer, Function<Boolean, ItemStack>> inputFilters = new HashMap<>();
    private Map<Integer, Function<Boolean, ItemStack>> outputFilters = new HashMap<>();

    private Map<Integer, Inventory> inventories = new HashMap<>();

    public CustomTileEntity(String name) {
        this.name = name;
    }



    public void addInventory(int index, int slots) {
        Inventory inv = Bukkit.createInventory(null, slots);
        inventories.put(index, inv);
    }

    public void addInputFilter(int index, Function<Boolean, ItemStack> filter) {
        this.inputFilters.put(index, filter);
    }

    public void addOutputFilter(int index, Function<Boolean, ItemStack> filter) {
        this.outputFilters.put(index, filter);
    }


    public abstract void onInteract(Player player, Block block);

    public abstract boolean tryInsert(ItemStack stack, Block block, Inventory inventory, BlockFace side);

    public boolean isInputEnabled() {
        return inputEnabled;
    }

    public void setInputEnabled(boolean inputEnabled) {
        this.inputEnabled = inputEnabled;
    }

    public boolean isOutputEnabled() {
        return outputEnabled;
    }

    public void setOutputEnabled(boolean outputEnabled) {
        this.outputEnabled = outputEnabled;
    }

    public String getName() {
        return name;
    }
}
