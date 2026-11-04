package net.morthen.resourceconfigapi.api;

import net.minecraft.resources.ResourceLocation;
import net.morthen.resourceconfigapi.api.annotation.Config;
import net.morthen.resourceconfigapi.impl.ConfigManager;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Entry point of the api, configs are registered and looked up here. */
public final class ConfigRegistry {

    private ConfigRegistry() {}

    /**
     * Registers the config held by the given class under {@code modId}, loads the file
     * and applies the values to the fields.
     * @param clazz the class annotated with {@link Config} holding the entries
     * @param modId namespace the config is registered under
     * @return the registered config
     * @throws IllegalArgumentException if the class is not annotated with {@link Config} or the file name is invalid
     * @throws IllegalStateException if a config is already registered under the same id
     */
    public static ConfigFile register(Class<?> clazz, String modId) {
        return ConfigManager.register(clazz, modId);
    }

    public static Optional<ConfigFile> get(ResourceLocation id) {
        return ConfigManager.get(id);
    }

    /** @return all configs in registration order */
    public static Collection<ConfigFile> getAll() {
        return ConfigManager.getAll();
    }

    /** @return the configs of the mod in registration order */
    public static List<ConfigFile> getConfigs(String modId) {
        return ConfigManager.getAll().stream().filter(config -> config.modId().equals(modId)).toList();
    }

    /** @return the configs of the mod with the given type in registration order */
    public static List<ConfigFile> getConfigs(String modId, ConfigType type) {
        return getConfigs(modId).stream().filter(config -> config.type() == type).toList();
    }

    public static List<String> getRegisteredMods() {
        return ConfigManager.getRegisteredMods();
    }
}
