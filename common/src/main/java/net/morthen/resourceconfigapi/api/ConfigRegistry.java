package net.morthen.resourceconfigapi.api;

import net.minecraft.resources.Identifier;
import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.api.annotations.Config;
import net.morthen.resourceconfigapi.impl.ModConfigFile;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public final class ConfigRegistry {
    private static final Map<Identifier, ConfigFile> CONFIGS = new LinkedHashMap<>();
    private static final Pattern VALID_NAME = Pattern.compile("[a-z0-9_.-]+");

    private ConfigRegistry() {}

    public static synchronized ConfigFile register(Class<?> clazz, String modId) {
        Config config = clazz.getAnnotation(Config.class);
        if (config == null) {
            throw new IllegalArgumentException("Cannot register '" + clazz.getName()
                    + "', it isn't annotated with @Config");
        }

        Identifier id = createId(modId, config);
        if (CONFIGS.containsKey(id)) {
            throw new IllegalStateException("A config is already registered as " + id + ", '"
                    + clazz.getName() + "' needs a different fileName");
        }

        ModConfigFile file = new ModConfigFile(clazz, config, id, modId);
        CONFIGS.put(id, file);
        ConfigConstants.LOGGER.debug("Registered config {}", id);

        return file;
    }

    public static synchronized Collection<ConfigFile> getAll() {
        return CONFIGS.values();
    }

    public static synchronized Optional<ConfigFile> get(Identifier id) {
        return Optional.ofNullable(CONFIGS.get(id));
    }

    public static synchronized Collection<ConfigFile> get(String modId) {
        return getAll().stream().filter(config -> config.modId().equals(modId)).toList();
    }

    public static synchronized Collection<ConfigFile> get(String modId, ConfigType type) {
        return get(modId).stream().filter(config -> config.type() == type).toList();
    }

    private static Identifier createId(String modId, Config config) {
        if (!VALID_NAME.matcher(config.fileName()).matches()) {
            throw new IllegalArgumentException("Invalid fileName '" + config.fileName()
                    + "', only lowercase letters, digits, '_', '-' and '.' are allowed");
        }

        return Identifier.fromNamespaceAndPath(modId, config.type().getDirectory() + "/" + config.fileName());
    }
}
