package com.strangeone101.holoitems.items.implementations;

import com.strangeone101.holoitems.CustomItem;
import org.bukkit.Material;

public class RepairableItem extends CustomItem {

    private CustomItem repairItem;

    public RepairableItem(String name, Material material, CustomItem repairItem) {
        super(name, material);

        this.repairItem = repairItem;
    }

    public CustomItem getRepairItem() {
        return repairItem;
    }
}
