package com.strangeone101.holoitemsapi.util;

import com.strangeone101.holoitemsapi.HoloItemsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Shulker;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class AbilityUtils {

    private static boolean setup;
    private static NamespacedKey GHOST_KEY;
    private static Scoreboard GHOST_SCOREBOARD;

    private static Map<Block, Shulker> GHOSTS = new HashMap<>();
    private static Map<ChatColor, Team> TEAMS = new HashMap<>();

    private static void setup() {
        GHOST_KEY = new NamespacedKey(HoloItemsAPI.getPlugin(), "ShulkerGhost");
        GHOST_SCOREBOARD = Bukkit.getScoreboardManager().getNewScoreboard();
        setup = true;
    }

    public static boolean isGhostBlock(Entity entity) {
        if (!setup) setup();

        return GHOSTS.containsValue(entity) || entity.getPersistentDataContainer().has(GHOST_KEY, PersistentDataType.BYTE);
    }

    public static void createGhostBlock(Block block) {
        createGhostBlock(block, ChatColor.WHITE);
    }

    public static void createGhostBlock(Block block, ChatColor color) {
        Shulker shulker = block.getWorld().spawn(block.getLocation(), Shulker.class);
        shulker.setAI(false);
        shulker.setInvisible(true);
        shulker.setCollidable(false);
        shulker.setGlowing(true);
        shulker.setGravity(false);
        shulker.setSilent(true);
        shulker.getPersistentDataContainer().set(GHOST_KEY, PersistentDataType.BYTE, (byte)1);

        if (color.isColor() && color != ChatColor.WHITE) {
            if (!TEAMS.containsKey(color)) {
                Team newTeam = GHOST_SCOREBOARD.registerNewTeam("ghost_" + color.toString().toLowerCase());
                newTeam.setColor(color);
                TEAMS.put(color, newTeam);
            }

            Team team = TEAMS.get(color);
            team.addEntry(shulker.getUniqueId().toString());

        }

        GHOSTS.put(block, shulker);
    }

    public static void removeGhostBlock(Block block) {
        if (GHOSTS.containsKey(block)) {
            Shulker shulker = GHOSTS.get(block);
            shulker.remove();
            GHOSTS.remove(block);
        }
    }

}
