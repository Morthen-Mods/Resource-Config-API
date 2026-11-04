package net.morthen.resourceconfigapi.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.api.ConfigRegistry;
import net.morthen.resourceconfigapi.client.screen.ConfigScreen;

import java.util.HashMap;
import java.util.Map;

public class ConfigAPIModMenu implements ModMenuApi {
    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        Map<String, ConfigScreenFactory<?>> factoryMap = new HashMap<>();

        ConfigRegistry.getRegisteredMods().forEach(modId -> {
            factoryMap.putIfAbsent(modId, screen -> new ConfigScreen(screen, modId));
        });

        return factoryMap;
    }
}
