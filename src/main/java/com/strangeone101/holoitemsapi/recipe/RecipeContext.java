package com.strangeone101.holoitemsapi.recipe;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

public class RecipeContext {

    private Player player;
    private Location location;
    private World world;
    private boolean isRightClick;
    private boolean isShiftClick;
    private Recipe recipe;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public boolean isRightClick() {
        return isRightClick;
    }

    public void setRightClick(boolean rightClick) {
        isRightClick = rightClick;
    }

    public boolean isShiftClick() {
        return isShiftClick;
    }

    public void setShiftClick(boolean shiftClick) {
        isShiftClick = shiftClick;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
