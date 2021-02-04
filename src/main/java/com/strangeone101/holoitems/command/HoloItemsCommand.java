package com.strangeone101.holoitems.command;

import com.strangeone101.holoitems.CustomItem;
import com.strangeone101.holoitems.CustomItemRegistry;
import com.strangeone101.holoitems.items.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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

        int amount = CustomItemRegistry.getCustomItems().size();
        int rows = ((amount - 1) / 9) + 1;

        Inventory inv = Bukkit.createInventory(null, rows * 9, "HoloItems List");

        for (CustomItem ci : CustomItemRegistry.getCustomItems().values()) {
            inv.addItem(ci.buildStack((Player) sender));
        }

        ((Player)sender).openInventory(inv);

        return true;
    }
}
