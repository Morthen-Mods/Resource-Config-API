package net.morthen.resourceconfigapi.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an {@code int} {@link ConfigEntry} as color. The config screen shows a color picker and
 * the file stores the value as hex string, {@code "#RRGGBB"} or {@code "#AARRGGBB"} with alpha.
 * <pre>{@code
 * @ConfigEntry
 * @ColorEntry
 * public static int highlightColor = 0xFF8800;
 *
 * @ConfigEntry
 * @ColorEntry(alpha = true)
 * public static int overlayColor = 0x80000000;
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ColorEntry {

    /** @return true if the color has an alpha channel, otherwise only the lower 24 bits are used */
    boolean alpha() default false;
}
