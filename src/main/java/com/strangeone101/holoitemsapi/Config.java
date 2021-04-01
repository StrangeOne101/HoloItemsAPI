package com.strangeone101.holoitemsapi;

import com.strangeone101.holoitemsapi.util.CustomDamageSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    static FileConfiguration deathMessageConfig;
    static FileConfiguration generalConfig;
    static File deathMessageConfigFile;
    static File generalConfigFile;

    public static boolean hasDeathMessageConfig() {
        return deathMessageConfig != null;
    }

    public static boolean hasGeneralConfig() {
        return generalConfig != null;
    }

    public static boolean addDefaultDeathMessage(CustomDamageSource source) {
        if (!hasDeathMessageConfig()) {
            return false;
        }

        deathMessageConfig.addDefault(source.getName() + ".Player", "{player} was killed by {killer}'s " + source.getName());
        deathMessageConfig.addDefault(source.getName() + ".Other", "{player} was killed by " + source.getName());
        deathMessageConfig.addDefault(source.getName() + ".Self", "{player} killed themselves with " + source.getName());

        try {
            deathMessageConfig.save(deathMessageConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getDeathMessage(CustomDamageSource source, String type) {
        if (type == null) type = "Other";
        String defSelf = "{player} killed themselves with " + source.getName();
        String defOther = "{player} was killed by " + source.getName();
        String defPlayer = "{player} was killed by {killer}'s " + source.getName();
        if (hasGeneralConfig()) {
            if (type.equalsIgnoreCase("other")) return deathMessageConfig.getString(source.getName() + ".Other", defOther);
            else if (type.equalsIgnoreCase("self")) return deathMessageConfig.getString(source.getName() + ".Self", defSelf);
            return deathMessageConfig.getString(source.getName() + ".Player", defPlayer);
        }

        if (type.equalsIgnoreCase("other")) return defOther;
        else if (type.equalsIgnoreCase("self")) return defSelf;
        return defPlayer;

    }

    public static FileConfiguration getGeneralConfig() {
        return generalConfig;
    }

    public static boolean addDefaultConfigOption(String path, Object default_) {
        if (!hasGeneralConfig()) {
            return false;
        }

        generalConfig.addDefault(path, default_);
        try {
            generalConfig.save(generalConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean addDefaultConfigOption(CustomItem item, String option, Object default_) {
        return addDefaultConfigOption("Items." + item.getInternalName() + "." + option, default_);
    }


}
