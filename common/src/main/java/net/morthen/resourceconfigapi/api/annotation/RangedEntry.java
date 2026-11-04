package net.morthen.resourceconfigapi.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Limits a numeric {@link ConfigEntry} to a range. Values outside of it are clamped when the
 * config is loaded and when a value is set. Ranged options are shown as slider in the config screen.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RangedEntry {

    /** @return minimum value, inclusive */
    double minValue();

    /** @return maximum value, inclusive */
    double maxValue();
}
