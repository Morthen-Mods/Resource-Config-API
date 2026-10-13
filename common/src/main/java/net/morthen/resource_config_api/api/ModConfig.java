package net.morthen.resource_config_api.api;

import com.google.gson.*;
import net.minecraft.resources.Identifier;
import net.morthen.resource_config_api.ConfigConstants;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * One registered config file. The annotated static fields are the live values;
 * this class reads and writes them, persists them as JSON and converts raw input
 * (strings from the screen, JSON from disk or the network) to the field types.
 */
public final class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Set<Class<?>> SUPPORTED_TYPES = Set.of(
            boolean.class, Boolean.class, byte.class, Byte.class, short.class, Short.class,
            int.class, Integer.class, long.class, Long.class, float.class, Float.class,
            double.class, Double.class, String.class);

    private final Identifier id;
    private final String modId;
    private final String fileName;
    private final ConfigType type;
    private final Path file;
    private final Map<String, Field> fields = new LinkedHashMap<>();
    private final Map<String, Object> defaults = new HashMap<>();

    ModConfig(Class<?> clazz, String modId, Path configDir) {
        Config config = clazz.getAnnotation(Config.class);
        this.modId = modId;
        this.fileName = config.fileName();
        this.type = config.type();
        this.id = Identifier.fromNamespaceAndPath(modId, type + "/" + fileName.toLowerCase(Locale.ROOT));
        this.file = configDir.resolve(modId).resolve(type.toString()).resolve(fileName + ".json");

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigEntry.class)) continue;

            int mods = field.getModifiers();
            Class<?> fieldType = field.getType();
            if (!Modifier.isStatic(mods) || Modifier.isFinal(mods)
                    || !(fieldType.isEnum() || SUPPORTED_TYPES.contains(fieldType))) {
                ConfigConstants.LOGGER.warn("Skipping config entry {}.{}: it must be a static non-final boolean, number, String or enum",
                        clazz.getName(), field.getName());
                continue;
            }

            field.setAccessible(true);
            fields.put(field.getName(), field);
            defaults.put(field.getName(), get(field.getName()));
        }
    }

    /** Resets every entry to its default, then applies the values stored on disk. */
    public void load() {
        defaults.forEach(this::set);
        if (!Files.exists(file)) return;

        try (Reader reader = Files.newBufferedReader(file)) {
            apply(JsonParser.parseReader(reader).getAsJsonObject());
        } catch (IOException | JsonParseException | IllegalStateException e) {
            ConfigConstants.LOGGER.error("Failed to read config {}, using defaults", file, e);
        }
    }

    public void save() {
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(toJson()));
        } catch (IOException e) {
            ConfigConstants.LOGGER.error("Failed to save config {}", file, e);
        }
    }

    /** Applies every known key of {@code json}; unknown keys and invalid values are ignored. */
    public void apply(JsonObject json) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (fields.containsKey(entry.getKey()) && !set(entry.getKey(), entry.getValue())) {
                ConfigConstants.LOGGER.warn("Ignoring invalid value {} for {} in {}", entry.getValue(), entry.getKey(), id);
            }
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        fields.keySet().forEach(key -> json.add(key, GSON.toJsonTree(get(key))));
        return json;
    }

    /** Serializes values keyed by entry name (e.g. pending edits) the same way {@link #toJson()} does. */
    public static JsonObject toJson(Map<String, Object> values) {
        return GSON.toJsonTree(values).getAsJsonObject();
    }

    /**
     * Converts {@code raw} (a value of any JSON-compatible type, a string or a {@link JsonElement})
     * to the type of the entry, clamped to its {@link RangedEntry}.
     * @return the converted value, or {@code null} if {@code raw} is not valid for this entry
     */
    public Object parse(String key, Object raw) {
        Field field = field(key);
        try {
            Object value = GSON.fromJson(GSON.toJsonTree(raw), field.getType());
            RangedEntry range = field.getAnnotation(RangedEntry.class);
            if (range != null && value instanceof Number number) {
                double clamped = Math.clamp(number.doubleValue(), range.minValue(), range.maxValue());
                value = GSON.fromJson(GSON.toJsonTree(clamped), field.getType());
            }
            return value;
        } catch (JsonParseException | NumberFormatException e) {
            return null;
        }
    }

    /** @return {@code false} if {@code raw} is not a valid value for the entry */
    public boolean set(String key, Object raw) {
        Object value = parse(key, raw);
        if (value == null) return false;

        try {
            field(key).set(null, value);
            return true;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public Object get(String key) {
        try {
            return field(key).get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public Object getDefault(String key) {
        return defaults.get(key);
    }

    public Set<String> keys() {
        return fields.keySet();
    }

    public Class<?> valueType(String key) {
        return field(key).getType();
    }

    public ConfigEntry entry(String key) {
        return field(key).getAnnotation(ConfigEntry.class);
    }

    public RangedEntry range(String key) {
        return field(key).getAnnotation(RangedEntry.class);
    }

    public Identifier id() {
        return id;
    }

    public String modId() {
        return modId;
    }

    public String fileName() {
        return fileName;
    }

    public ConfigType type() {
        return type;
    }

    /**
     * {@code config.<modId>.<fileName>}; append {@code .label} or {@code .tooltip} for the file
     * and {@code .<category>.label} for a category.
     */
    public String translationKey() {
        return "config." + modId + "." + fileName;
    }

    /** Translation key of an entry; append {@code .label} or {@code .tooltip}. */
    public String translationKey(String key) {
        ConfigEntry entry = entry(key);
        if (!entry.translation().isBlank()) return translationKey() + "." + entry.translation();
        if (!entry.category().isBlank()) return translationKey() + "." + entry.category() + "." + key;
        return translationKey() + "." + key;
    }

    private Field field(String key) {
        Field field = fields.get(key);
        if (field == null) throw new IllegalArgumentException("Unknown config entry " + key + " in " + id);
        return field;
    }
}
