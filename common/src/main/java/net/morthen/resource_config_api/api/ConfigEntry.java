package net.morthen.resource_config_api.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@code static} non-final field as a config entry.
 * Supported types are booleans, numbers, {@link String} and enums.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigEntry {

    /** Category the entry is grouped under in the config screen. */
    String category() default "";

    EntryImpact impact() default EntryImpact.DIRECT;

    /**
     * Shared translation key for entries that have the same label and tooltip.
     * <p>
     * Example: three solar panel tiers in different categories all have a
     * {@code generateByDay} entry. With {@code translation = "generateByDay"} they all use
     * {@code config.<modId>.<fileName>.generateByDay.label} (and {@code .tooltip})
     * instead of one key per category.
     */
    String translation() default "";
}
