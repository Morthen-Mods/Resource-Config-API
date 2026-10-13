package net.morthen.resource_config_api.api;

import net.minecraft.resources.Identifier;
import net.morthen.resource_config_api.ConfigConstants;
import net.morthen.resource_config_api.service.Services;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ConfigRegistry {
    private static final Map<Identifier, ModConfig> CONFIGS = new LinkedHashMap<>();

    private ConfigRegistry() {}

    /**
     * Registers the config held by {@code clazz} under {@code modId}, loads it from
     * {@code config/<modId>/<type>/<fileName>.json} and writes the file back with any missing entries.
     * Call this from your main (not client) initializer so the config exists on both sides;
     * a dedicated server keeps CLIENT configs at their defaults and never writes their files.
     * @throws IllegalArgumentException if the class is not annotated with {@link Config}
     * @throws IllegalStateException if a config with the same type and file name is already registered for the mod
     */
    public static ModConfig register(Class<?> clazz, String modId) {
        if (!clazz.isAnnotationPresent(Config.class)) {
            throw new IllegalArgumentException(clazz.getName() + " is missing the @Config annotation");
        }

        ModConfig config = new ModConfig(clazz, modId, Services.PLATFORM.getConfigDir());
        if (CONFIGS.putIfAbsent(config.id(), config) != null) {
            throw new IllegalStateException("A config is already registered as " + config.id()
                    + ", " + clazz.getName() + " needs a different fileName");
        }

        // a dedicated server keeps the defaults of CLIENT configs and never creates their files
        if (config.type() == ConfigType.COMMON || !Services.PLATFORM.isDedicatedServer()) {
            config.load();
            config.save();
        }
        ConfigConstants.LOGGER.debug("Registered config {}", config.id());
        return config;
    }

    public static ModConfig get(Identifier id) {
        return CONFIGS.get(id);
    }

    public static Collection<ModConfig> all() {
        return Collections.unmodifiableCollection(CONFIGS.values());
    }
}
