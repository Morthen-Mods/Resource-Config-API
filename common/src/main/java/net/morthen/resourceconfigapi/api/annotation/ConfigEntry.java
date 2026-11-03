package net.morthen.resourceconfigapi.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@code public static}, non final field as config option. The initial value of the
 * field is the default value and must not be null. Supported in the config screen are
 * booleans, numbers, strings and enums, other types are saved but can't be edited in the ui.
 * Collections and maps aren't supported.
 * <p>
 * Translation keys of an option:
 * <ul>
 *     <li>{@code config.{modId}.{fileName}.{category}.{fieldName}.label}</li>
 *     <li>{@code config.{modId}.{fileName}.{category}.{fieldName}.tooltip}</li>
 * </ul>
 * The category part is left out for options without category.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigEntry {

    /**
     * Category the option is grouped under in the config screen. Options without a category are
     * shown first, the categories follow in the order of their first option.<br>
     * Only lowercase letters, digits, {@code _}, {@code -} and {@code .} are allowed.
     * The label of a category uses the key {@code config.{modId}.{fileName}.{category}.label}.
     * @return category name, empty for none
     */
    String category() default "";

    /** @return true if a change only takes effect after restarting the game */
    boolean needsGameRestart() default false;

    /** @return true if a change only takes effect after rejoining the world */
    boolean needsWorldRestart() default false;

    /**
     * Shared translation key for options that have the same meaning, the label and tooltip keys
     * become {@code config.{modId}.{fileName}.{translation}.label} and {@code .tooltip}.<br>
     * Example: three solar panels with the same options in different categories.
     * <pre>{@code
     * @Config(fileName = "solar_panels", type = ConfigType.COMMON)
     * public class SolarPanels {
     *      @ConfigEntry(category = "basic", translation = "generate_by_day")
     *      public static int basicGenerateByDay = 25;
     *
     *      @ConfigEntry(category = "advanced", translation = "generate_by_day")
     *      public static int advancedGenerateByDay = 100;
     *
     *      @ConfigEntry(category = "industrial", translation = "generate_by_day")
     *      public static int industrialGenerateByDay = 400;
     * }
     * }</pre>
     * All three options use {@code config.{modId}.solar_panels.generate_by_day.label}, so the
     * translation only has to be added once instead of three times.
     *
     * @return the shared translation key, empty to use the field name
     */
    String translation() default "";
}
