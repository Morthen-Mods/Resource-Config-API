package net.morthen.resourceconfigapi.impl;

import net.minecraft.resources.Identifier;
import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.api.ConfigFile;
import net.morthen.resourceconfigapi.api.ConfigRegistry;
import net.morthen.resourceconfigapi.api.annotation.Config;
import net.morthen.resourceconfigapi.impl.platform.ConfigServices;

import java.util.*;

/** Holds the registered configs, use {@link ConfigRegistry} instead. */
public final class ConfigManager {
    private static final Map<Identifier, ConfigFile> CONFIGS = new LinkedHashMap<>();
    private static final Collection<ConfigFile> CONFIGS_VIEW = Collections.unmodifiableCollection(CONFIGS.values());
    private static final List<String> REGISTERED_MODS = new ArrayList<>();

    private ConfigManager() {}

    public static synchronized ConfigFile register(Class<?> clazz, String modId) {
        Config config = clazz.getAnnotation(Config.class);
        if (config == null) {
            throw new IllegalArgumentException("Cannot register '" + clazz.getName() + "', it isn't annotated with @Config");
        }

        if (!REGISTERED_MODS.contains(modId)) REGISTERED_MODS.add(modId);

        Identifier id = ModConfigFile.createId(modId, config);
        if (CONFIGS.containsKey(id)) {
            throw new IllegalStateException("A config is already registered as " + id
                    + ", '" + clazz.getName() + "' needs a different fileName");
        }

        ModConfigFile file = new ModConfigFile(clazz, config, modId, ConfigServices.PLATFORM.getConfigDir());
        CONFIGS.put(id, file);
        ConfigConstants.LOGGER.debug("Registered config {}", id);

        return file;
    }

    public static synchronized Optional<ConfigFile> get(Identifier id) {
        return Optional.ofNullable(CONFIGS.get(id));
    }

    public static Collection<ConfigFile> getAll() {
        return CONFIGS_VIEW;
    }

    public static List<String> getRegisteredMods() {
        return REGISTERED_MODS;
    }
}
