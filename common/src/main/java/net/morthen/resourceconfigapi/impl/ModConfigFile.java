package net.morthen.resourceconfigapi.impl;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.api.ConfigFile;
import net.morthen.resourceconfigapi.api.ConfigOption;
import net.morthen.resourceconfigapi.api.ConfigType;
import net.morthen.resourceconfigapi.api.annotation.Config;
import net.morthen.resourceconfigapi.api.annotation.ConfigEntry;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Config backed by the {@code public static} fields of a class and stored as json file.
 * <p>
 * Note: the options are read with {@link Class#getDeclaredFields()}, which returns the fields in
 * declaration order on all common JVMs, even though the specification doesn't guarantee it.
 */
public final class ModConfigFile implements ConfigFile {
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Pattern VALID_NAME = Pattern.compile("[a-z0-9_.-]+");

    private final ResourceLocation id;
    private final String modId;
    private final String fileName;
    private final ConfigType type;
    private final Path path;
    private final String translationBase;

    private final Map<String, FieldConfigOption<?>> options = new LinkedHashMap<>();
    private final List<ConfigOption<?>> optionList;
    private final List<String> categories;

    private JsonObject data = new JsonObject();
    private boolean serverValues;

    ModConfigFile(Class<?> clazz, Config config, String modId, Path configDir) {
        this.id = createId(modId, config);
        this.modId = modId;
        this.fileName = config.fileName();
        this.type = config.type();
        this.path = configDir.resolve(modId).resolve(this.type.getDirectoryName()).resolve(this.fileName + ".json");
        this.translationBase = "config." + modId + "." + this.fileName;

        this.collectOptions(clazz);
        this.optionList = List.copyOf(this.options.values());

        Set<String> categories = new LinkedHashSet<>();
        this.options.values().forEach(option -> {
            if (!option.category().isEmpty()) categories.add(option.category());
        });
        this.categories = List.copyOf(categories);

        this.load();
        this.save();
    }

    /**
     * @throws IllegalArgumentException if the file name contains invalid characters
     */
    static ResourceLocation createId(String modId, Config config) {
        requireValidName(config.fileName(), "fileName");
        return new ResourceLocation(modId, config.type().getDirectoryName() + "/" + config.fileName());
    }

    static void requireValidName(String name, String what) {
        if (!VALID_NAME.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid " + what + " '" + name
                    + "', only lowercase letters, digits, '_', '-' and '.' are allowed");
        }
    }

    // --------------------------------------------------------------------------------------------
    // Setup
    // --------------------------------------------------------------------------------------------

    private void collectOptions(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            ConfigEntry entry = field.getAnnotation(ConfigEntry.class);
            if (entry == null || !isValidField(field)) continue;

            this.options.put(field.getName(), FieldConfigOption.create(this, field, entry));
        }
    }

    private static boolean isValidField(Field field) {
        int mods = field.getModifiers();
        String name = field.getDeclaringClass().getSimpleName() + "." + field.getName();

        if (!Modifier.isPublic(mods) || !Modifier.isStatic(mods) || Modifier.isFinal(mods)) {
            ConfigConstants.LOGGER.warn("Config entry {} must be public, static and not final, skipping it", name);
            return false;
        }

        Class<?> type = field.getType();
        if (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type) || type.isArray()) {
            ConfigConstants.LOGGER.warn("Config entry {} has the unsupported type {}, skipping it", name, type.getSimpleName());
            return false;
        }

        try {
            if (field.get(null) == null) {
                ConfigConstants.LOGGER.warn("Config entry {} has no default value, skipping it", name);
                return false;
            }
        } catch (IllegalAccessException e) {
            ConfigConstants.LOGGER.error("Failed to access config entry {}, skipping it", name, e);
            return false;
        }

        return true;
    }

    // --------------------------------------------------------------------------------------------
    // Loading / Saving
    // --------------------------------------------------------------------------------------------

    /**
     * Reads the file and applies every value to its field. Missing or invalid values are replaced
     * with the default, numbers are clamped to their range and unknown keys are dropped.
     */
    private void load() {
        JsonObject file = this.readFile();
        JsonObject result = new JsonObject();

        for (FieldConfigOption<?> option : this.options.values()) {
            Object value = option.clamp(option.parse(file == null ? null : file.get(option.key())));

            option.setField(value);
            result.add(option.key(), option.toJson(value));
        }

        this.data = result;
    }

    /** @return the content of the file, or null if it is missing, empty or corrupt */
    private JsonObject readFile() {
        if (!Files.exists(this.path)) return null;

        try (Reader reader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, JsonObject.class);
        } catch (IOException | JsonParseException e) {
            ConfigConstants.LOGGER.error("Failed to read config {}, falling back to defaults", this.path, e);
            return null;
        }
    }

    @Override
    public void save() {
        // the values of a server must never end up in the local file
        if (this.serverValues) {
            ConfigConstants.LOGGER.debug("Not saving {}, it holds the values of the server", this.id);
            return;
        }

        try {
            Files.createDirectories(this.path.getParent());

            // written to a temp file first, so a crash while writing can't corrupt the config
            Path temp = this.path.resolveSibling(this.path.getFileName() + ".tmp");
            Files.writeString(temp, GSON.toJson(this.data), StandardCharsets.UTF_8);
            try {
                Files.move(temp, this.path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, this.path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            ConfigConstants.LOGGER.error("Failed to save config {}", this.path, e);
        }
    }

    @Override
    public void reload() {
        this.serverValues = false;
        this.load();
    }

    // --------------------------------------------------------------------------------------------
    // Values
    // --------------------------------------------------------------------------------------------

    /**
     * Applies every known key of the json, unknown keys and invalid values are ignored.
     * The file is not written.
     */
    public void applyJson(JsonObject json) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            FieldConfigOption<?> option = this.options.get(entry.getKey());
            if (option == null) continue;

            Object value = option.parseOrNull(entry.getValue());
            if (value == null) {
                ConfigConstants.LOGGER.warn("Ignoring invalid value {} for {} in {}", entry.getValue(), entry.getKey(), this.id);
                continue;
            }
            this.set(option, value);
        }
    }

    /**
     * Applies the values of a server. They are only kept in memory, {@link #save()} does nothing
     * until {@link #reload()} restores the values of the local file.
     */
    public void applyServerValues(JsonObject json) {
        this.serverValues = true;
        this.applyJson(json);
    }

    /** @return the values of the given keys as they are stored in the file, unknown keys are skipped */
    public JsonObject toJson(Collection<String> keys) {
        JsonObject json = new JsonObject();
        for (String key : keys) {
            if (this.data.has(key)) json.add(key, this.data.get(key).deepCopy());
        }
        return json;
    }

    /** @return all values as they are stored in the file */
    public JsonObject toJson() {
        return this.data.deepCopy();
    }

    void set(FieldConfigOption<?> option, Object value) {
        Object converted = option.clamp(option.convert(value));

        option.setField(converted);
        this.data.add(option.key(), option.toJson(converted));
    }

    // --------------------------------------------------------------------------------------------
    // Getters
    // --------------------------------------------------------------------------------------------

    @Override
    public ResourceLocation id() {
        return this.id;
    }

    @Override
    public String modId() {
        return this.modId;
    }

    @Override
    public String fileName() {
        return this.fileName;
    }

    @Override
    public ConfigType type() {
        return this.type;
    }

    @Override
    public List<ConfigOption<?>> options() {
        return this.optionList;
    }

    @Override
    public Optional<ConfigOption<?>> option(String key) {
        return Optional.ofNullable(this.options.get(key));
    }

    @Override
    public List<String> categories() {
        return this.categories;
    }

    String translationBase() {
        return this.translationBase;
    }

    @Override
    public String labelKey() {
        return this.translationBase + ".label";
    }

    @Override
    public String tooltipKey() {
        return this.translationBase + ".tooltip";
    }

    @Override
    public String categoryLabelKey(String category) {
        return this.translationBase + "." + category + ".label";
    }

    @Override
    public String toString() {
        return "ModConfigFile[" + this.id + "]";
    }
}
