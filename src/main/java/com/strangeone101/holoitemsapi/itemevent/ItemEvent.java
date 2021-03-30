package com.strangeone101.holoitemsapi.itemevent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface ItemEvent {

    com.strangeone101.holoitemsapi.itemevent.Target target() default com.strangeone101.holoitemsapi.itemevent.Target.SELF;

    ActiveConditions active() default ActiveConditions.INVENTORY;
}
