package net.morthen.resource_config_api.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Limits a numeric {@link ConfigEntry}.
 * Values outside the range are clamped and the entry is shown as a slider.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RangedEntry {
    double minValue();

    double maxValue();
}
