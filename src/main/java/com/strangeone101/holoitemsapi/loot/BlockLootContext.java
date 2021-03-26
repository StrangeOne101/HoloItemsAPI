package com.strangeone101.holoitemsapi.loot;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BlockLootContext {

    private Block block;
    private int fortune;
    private double luck;
    private Player player;
    private boolean silk;

    public BlockLootContext(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public int getFortune() {
        return fortune;
    }

    public void setFortune(int fortune) {
        this.fortune = fortune;
    }

    public double getLuck() {
        return luck;
    }

    public void setLuck(double luck) {
        this.luck = luck;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean hasSilkTouch() {
        return silk;
    }

    public void setSilkTouch(boolean silk) {
        this.silk = silk;
    }
}
