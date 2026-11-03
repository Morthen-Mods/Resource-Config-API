package net.morthen.resourceconfigapi.api;

import net.morthen.resourceconfigapi.api.annotation.ColorEntry;

/**
 * How an {@code int} option is interpreted as color.
 * @see ColorEntry
 */
public enum ColorMode {
    /** Not a color. */
    NONE,
    /** {@code 0xRRGGBB} */
    RGB,
    /** {@code 0xAARRGGBB} */
    ARGB
}
