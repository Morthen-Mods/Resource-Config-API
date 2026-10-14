package net.morthen.resource_config_api;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.morthen.resource_config_api.api.ConfigRegistry;
import net.morthen.resource_config_api.api.ModConfig;
import net.morthen.resource_config_api.client.screen.ConfigScreen;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigApiModMenu implements ModMenuApi {
    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        Map<String, ConfigScreenFactory<?>> factoryMap = new HashMap<>();

        ConfigRegistry.all().forEach(config -> {
            factoryMap.putIfAbsent(config.modId(),
                    parent -> new ConfigScreen(parent, config.modId()));
        });

        return factoryMap;
    }
}
