package com.strangeone101.holoitemsapi.util;

import net.md_5.bungee.api.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class TranslationUtils {

    private static final Map<PotionEffectType, TranslatableComponent> POTION_EFFECTS = new HashMap<>();
    private static final Map<EntityType, TranslatableComponent> ENTITY_TYPES = new HashMap<>();

    private static boolean setup;

    private static void setup() {
        try {

            for (PotionEffectType type : PotionEffectType.values()) {
                String translationString = MobEffect.byId(type.getId()).getDescriptionId();
                TranslatableComponent component = new TranslatableComponent(translationString);
                POTION_EFFECTS.put(type, component);
            }

            for (EntityType type : EntityType.values()) {
                ENTITY_TYPES.put(type, new TranslatableComponent("entity.minecraft." + type.getName()));
            }

            setup = true;
        } catch (Exception e) {
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
