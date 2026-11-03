package net.morthen.resourceconfigapi.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.api.*;
import net.morthen.resourceconfigapi.api.annotation.ColorEntry;
import net.morthen.resourceconfigapi.api.annotation.ConfigEntry;
import net.morthen.resourceconfigapi.api.annotation.RangedEntry;

import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/** Option backed by a {@code public static} field. */
final class FieldConfigOption<T> implements ConfigOption<T> {
    private static final Set<Class<?>> INTEGRAL_TYPES = Set.of(Integer.class, Long.class, Short.class, Byte.class);

    private final ModConfigFile file;
    private final Field field;
    private final Class<?> boxedType;
    private final String category;
    private final Range range;
    private final ColorMode colorMode;
    private final RestartRequirement restartRequirement;
    private final String translationKey;
    private final JsonElement defaultJson;

    private FieldConfigOption(ModConfigFile file, Field field, ConfigEntry entry, JsonElement defaultJson) {
        this.file = file;
        this.field = field;
        this.boxedType = MethodType.methodType(field.getType()).wrap().returnType();
        this.category = entry.category();
        this.range = createRange(field, this.boxedType);
        this.colorMode = createColorMode(field, this.boxedType);
        this.restartRequirement = entry.needsGameRestart() ? RestartRequirement.GAME
                : entry.needsWorldRestart() ? RestartRequirement.WORLD
                : RestartRequirement.NONE;
        this.defaultJson = defaultJson;

        String name = entry.translation().isEmpty()
                ? (this.category.isEmpty() ? "" : this.category + ".") + field.getName()
                : entry.translation();
        this.translationKey = file.translationBase() + "." + name;
    }

    /**
     * @throws IllegalArgumentException if the category or translation key contains invalid characters
     */
    static FieldConfigOption<?> create(ModConfigFile file, Field field, ConfigEntry entry) {
        String name = field.getDeclaringClass().getSimpleName() + "." + field.getName();
        if (!entry.category().isEmpty()) ModConfigFile.requireValidName(entry.category(), "category of " + name);
        if (!entry.translation().isEmpty()) ModConfigFile.requireValidName(entry.translation(), "translation of " + name);

        try {
            JsonElement defaultJson = ModConfigFile.GSON.toJsonTree(field.get(null));
            return new FieldConfigOption<>(file, field, entry, defaultJson);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to read the default value of " + name, e);
        }
    }

    private static Range createRange(Field field, Class<?> boxedType) {
        RangedEntry ranged = field.getAnnotation(RangedEntry.class);
        if (ranged == null) return null;

        if (!Number.class.isAssignableFrom(boxedType)) {
            ConfigConstants.LOGGER.warn("@RangedEntry on the non numeric config entry {} is ignored", field.getName());
            return null;
        }

        try {
            return new Range(ranged.minValue(), ranged.maxValue());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid range on config entry " + field.getName() + ": " + e.getMessage(), e);
        }
    }

    private static ColorMode createColorMode(Field field, Class<?> boxedType) {
        ColorEntry color = field.getAnnotation(ColorEntry.class);
        if (color == null) return ColorMode.NONE;

        if (boxedType != Integer.class) {
            ConfigConstants.LOGGER.warn("@ColorEntry on the non int config entry {} is ignored", field.getName());
            return ColorMode.NONE;
        }
        return color.alpha() ? ColorMode.ARGB : ColorMode.RGB;
    }

    // --------------------------------------------------------------------------------------------
    // Value handling, used by ModConfigFile
    // --------------------------------------------------------------------------------------------

    /** @return the value stored in the json element, or the default if it is missing or invalid */
    Object parse(JsonElement element) {
        if (element == null || element.isJsonNull()) return this.defaultValue();

        Object value = this.parseOrNull(element);
        if (value != null) return value;

        ConfigConstants.LOGGER.warn("Invalid value {} for config entry {} in {}, using the default", element, this.key(), this.file.id());
        return this.defaultValue();
    }

    /** @return the value stored in the json element, or null if it is invalid */
    Object parseOrNull(JsonElement element) {
        if (element == null || element.isJsonNull()) return null;

        if (this.colorMode != ColorMode.NONE && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            Integer color = parseHexColor(element.getAsString());
            if (color != null) return this.colorMode == ColorMode.RGB ? color & 0xFFFFFF : color;
        }

        try {
            return ModConfigFile.GSON.fromJson(element, this.field.getType());
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * Converts the value into the boxed field type, numbers are converted between each other.
     * @throws IllegalArgumentException if the value is null or can't be converted
     */
    Object convert(Object value) {
        if (value == null) throw new IllegalArgumentException("Config entry " + this.key() + " can't be set to null");
        // rgb colors only use the lower 24 bits, an alpha channel would never match the stored value
        if (this.colorMode == ColorMode.RGB && value instanceof Integer color) return color & 0xFFFFFF;
        if (this.boxedType.isInstance(value)) return value;

        if (value instanceof Number number && Number.class.isAssignableFrom(this.boxedType)) {
            return this.toFieldType(number);
        }

        try {
            Object converted = ModConfigFile.GSON.fromJson(ModConfigFile.GSON.toJsonTree(value), this.field.getType());
            if (converted != null) return converted;
        } catch (RuntimeException ignored) {
            // thrown below
        }
        throw new IllegalArgumentException("Can't set config entry " + this.key() + " of type "
                + this.boxedType.getSimpleName() + " to " + value.getClass().getSimpleName() + " " + value);
    }

    /** Clamps numbers to the range of the option, other values are returned unchanged. */
    Object clamp(Object value) {
        if (this.range == null || !(value instanceof Number number)) return value;

        double min = this.range.min(), max = this.range.max();
        if (INTEGRAL_TYPES.contains(this.boxedType)) {
            // for whole numbers only the whole numbers inside the range are valid
            min = Math.ceil(min);
            max = Math.floor(max);
        }

        double original = number.doubleValue();
        if (original >= min && original <= max) return value;

        double clamped = Math.clamp(original, min, max);
        ConfigConstants.LOGGER.warn("Config entry {} = {} is outside of [{}, {}], clamping it", this.key(), number, min, max);
        return this.toFieldType(clamped);
    }

    private Object toFieldType(Number number) {
        Class<?> type = this.boxedType;
        if (type == Integer.class) return number.intValue();
        if (type == Long.class) return number.longValue();
        if (type == Short.class) return number.shortValue();
        if (type == Byte.class) return number.byteValue();
        if (type == Float.class) return number.floatValue();
        if (type == Double.class) return number.doubleValue();
        throw new IllegalArgumentException("Unsupported number type " + type.getSimpleName());
    }

    void setField(Object value) {
        try {
            this.field.set(null, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to set config entry " + this.key(), e);
        }
    }

    /** @return the value as it is written to the file, colors are stored as hex string */
    JsonElement toJson(Object value) {
        if (this.colorMode == ColorMode.RGB && value instanceof Integer color) {
            return new JsonPrimitive(String.format(Locale.ROOT, "#%06X", color & 0xFFFFFF));
        }
        if (this.colorMode == ColorMode.ARGB && value instanceof Integer color) {
            return new JsonPrimitive(String.format(Locale.ROOT, "#%08X", color));
        }
        return ModConfigFile.GSON.toJsonTree(value);
    }

    /** @return the color of a {@code #RRGGBB} or {@code #AARRGGBB} string, RGB colors get full alpha, or null if invalid */
    static Integer parseHexColor(String text) {
        String hex = text.startsWith("#") ? text.substring(1) : text;
        if (hex.length() != 6 && hex.length() != 8) return null;

        try {
            int color = Integer.parseUnsignedInt(hex, 16);
            return hex.length() == 6 ? 0xFF000000 | color : color;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // --------------------------------------------------------------------------------------------
    // ConfigOption
    // --------------------------------------------------------------------------------------------

    @Override
    public ConfigFile file() {
        return this.file;
    }

    @Override
    public String key() {
        return this.field.getName();
    }

    @Override
    public Class<?> type() {
        return this.field.getType();
    }

    @Override
    public String category() {
        return this.category;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        try {
            return (T) this.field.get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to read config entry " + this.key(), e);
        }
    }

    @Override
    public void set(T value) {
        this.file.set(this, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T defaultValue() {
        return (T) ModConfigFile.GSON.fromJson(this.defaultJson, this.field.getType());
    }

    @Override
    public Optional<Range> range() {
        return Optional.ofNullable(this.range);
    }

    @Override
    public RestartRequirement restartRequirement() {
        return this.restartRequirement;
    }

    @Override
    public ColorMode colorMode() {
        return this.colorMode;
    }

    @Override
    public String labelKey() {
        return this.translationKey + ".label";
    }

    @Override
    public String tooltipKey() {
        return this.translationKey + ".tooltip";
    }

    @Override
    public String toString() {
        return "FieldConfigOption[" + this.file.id() + "#" + this.key() + "]";
    }
}
