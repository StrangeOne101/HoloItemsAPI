package com.strangeone101.holoitemsapi.util;

import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TranslationUtils {

    private static final Map<PotionEffectType, TranslatableComponent> POTION_EFFECTS = new HashMap<>();
    private static final Map<EntityType, TranslatableComponent> ENTITY_TYPES = new HashMap<>();

    private static boolean setup;

    private static void setup() {
        try {
            Class mobEffectListClass = Class.forName(ReflectionUtils.nms + ".MobEffectList");
            Method fromIdMethod = mobEffectListClass.getDeclaredMethod("getId", Integer.TYPE);
            Method bMethod = mobEffectListClass.getDeclaredMethod("b");

            for (PotionEffectType type : PotionEffectType.values()) {
                Object mobEffectList = fromIdMethod.invoke(null, type.getId());
                String translationString = (String) bMethod.invoke(mobEffectList);
                TranslatableComponent component = new TranslatableComponent(translationString);
                POTION_EFFECTS.put(type, component);
            }

            for (EntityType type : EntityType.values()) {
                ENTITY_TYPES.put(type, new TranslatableComponent("entity.minecraft." + type.getName()));
            }

            setup = true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }

    public static TranslatableComponent getPotionEffect(PotionEffectType type) {
        if (!setup) setup();

        return POTION_EFFECTS.get(type);
    }

    public static TranslatableComponent getEntity(EntityType type) {
        if (!setup) setup();

        return ENTITY_TYPES.get(type);
    }
}
