package com.strangeone101.holoitems.items.implementations;

import com.strangeone101.holoitemsapi.CustomItem;
import com.strangeone101.holoitems.HoloItemsPlugin;
import com.strangeone101.holoitemsapi.interfaces.Placeable;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MegaSponge extends CustomItem implements Placeable {

    private int size;
    private Material suckMaterial;
    private CustomItem fullItem;

    public MegaSponge(String name, int size, Material suckMaterial, CustomItem full) {
        super(name, Material.SPONGE);
        this.size = size;
        this.suckMaterial = suckMaterial;
        this.fullItem = full;
    }

    @Override
    public boolean place(Block block, Player player, CustomItem item, ItemStack stack) {
        int amount = 0;
        for (int x = -size; x < size; x++) {
            for (int y = -size; y < size; y++) {
                for (int z = -size; z < size; z++) {
                    Block b = block.getWorld().getBlockAt(block.getX() + x, block.getY() + y, block.getZ() + z);
                    if (b.getType() == suckMaterial) {
                        b.setType(Material.AIR);
                        amount++;
                    } else if (suckMaterial == Material.WATER) {
                        if (b.getType() == Material.KELP_PLANT || b.getType() == Material.SEAGRASS) {
                            b.breakNaturally();
                        } else if (b.getState().getBlockData() instanceof Waterlogged && ((Waterlogged)b.getState().getBlockData()).isWaterlogged()) {
                            BlockData data = b.getBlockData();
                            ((Waterlogged)data).setWaterlogged(false);
                            b.setBlockData(data);
                        }
                    }
                }
            }
        }

        if (amount > 0) {
            if (player.getGameMode() != GameMode.CREATIVE) {
                stack.setAmount(stack.getAmount() - 1);
                if (stack.getAmount() == 0) {
                    stack.setType(Material.AIR);
                }
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    block.setType(Material.AIR);
                }
            }.runTaskLater(HoloItemsPlugin.INSTANCE, 1L);

            ItemStack drop = fullItem.buildStack(player);
            Location loc = block.getLocation().subtract(0.5, 0.5, 0.5);
            block.getWorld().dropItemNaturally(loc, drop);
            block.getWorld().playSound(loc, Sound.BLOCK_CORAL_BLOCK_BREAK, 1F, 1F);
        }
        return true;
    }
}
