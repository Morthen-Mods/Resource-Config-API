package net.morthen.resourceconfigapi.api;

import java.util.Optional;

/**
 * A single option of a {@link ConfigFile}, backed by a {@code public static} field.
 * @param <T> the boxed type of the field
 */
public interface ConfigOption<T> {

    ConfigFile file();

    /** @return the field name, also used as key in the json file */
    String key();

    /** @return the field type, for primitive fields the primitive class like {@code int.class} */
    Class<?> type();

    /** @return the category, empty if the option has none */
    String category();

    T get();

    /**
     * Sets the value of the field. Numbers are converted to the type of the field and clamped
     * to the {@link #range()}. The file is not written, call {@link ConfigFile#save()} for that.
     * @throws IllegalArgumentException if the value is null or can't be converted to the field type
     */
    void set(T value);

    T defaultValue();

    Optional<Range> range();

    RestartRequirement restartRequirement();

    /** @return the color mode, {@link ColorMode#NONE} if the option isn't a color */
    ColorMode colorMode();

    String labelKey();

    String tooltipKey();
}
