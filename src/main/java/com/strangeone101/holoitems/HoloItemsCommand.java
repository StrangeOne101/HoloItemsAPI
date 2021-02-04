package com.strangeone101.holoitems;

import com.strangeone101.holoitems.items.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HoloItemsCommand implements CommandExecutor {

    //This entire command class is from the test plugin. Please feel free to scrap it.

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "You must provide a player to give the item to!");
                return true;
            }
        }

        Player player;

        if (args.length > 0) {
            player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                sender.sendMessage(ChatColor.RED + "The player you specified does not exist!");
                return true;
            }
        } else {
            player = (Player) sender;
        }

        player.getInventory().addItem(Items.RUSHIA_SHIELD.buildStack(player));
        sender.sendMessage(ChatColor.YELLOW + "Custom item received!");

        return true;
    }
}
