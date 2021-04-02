package com.strangeone101.holoitemsapi.itemevent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.METHOD)

/**
 * A custom event annotation. Should be placed above any methods that wish
 * to listen to bukkit events. The method should have {@link EventContext}
 * and any bukkit event as parameters.
 */
public @interface ItemEvent {

    /**
     * What player is the event targeting? Should be `SELF`, `WORLD` or `ALL`
     * @return The event target
     */
    Target target() default Target.SELF;

    /**
     * The conditions that the item should be considered active and trigger
     * events.
     * @return The active item condition
     */
    ActiveConditions active() default ActiveConditions.INVENTORY;
}
