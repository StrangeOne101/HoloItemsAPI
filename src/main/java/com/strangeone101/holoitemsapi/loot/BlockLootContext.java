package com.strangeone101.holoitemsapi.loot;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * The context of when a block is broken. Used within {@link BlockLootTable}
 */
public class BlockLootContext {

    private Block block;
    private int fortune;
    private double luck;
    private Player player;
    private boolean silk;

    public BlockLootContext(Block block) {
        this.block = block;
    }

    /**
     * The block broken
     * @return The block
     */
    public Block getBlock() {
        return block;
    }

    /**
     * The level of fortune used when breaking this block.
     * @return The fortune level
     */
    public int getFortune() {
        return fortune;
    }

    /**
     * Set the fortune used when breaking the block
     * @param fortune The fortune level
     */
    public void setFortune(int fortune) {
        this.fortune = fortune;
    }

    /**
     * Get the level of luck the player has when they broke the block
     * @return The luck level
     */
    public double getLuck() {
        return luck;
    }

    /**
     * Set the level of luck the player has when they broke the block
     * @param luck The luck level
     */
    public void setLuck(double luck) {
        this.luck = luck;
    }

    /**
     * The player that broke the block
     * @return The player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Set the player that broke the block
     * @param player The player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Whether the block was broken with silk touch or not
     * @return True if silk touch was used
     */
    public boolean hasSilkTouch() {
        return silk;
    }

    /**
     * Set whether the block was broken with silk touch or not
     * @param silk If silk touch was used
     */
    public void setSilkTouch(boolean silk) {
        this.silk = silk;
    }
}
