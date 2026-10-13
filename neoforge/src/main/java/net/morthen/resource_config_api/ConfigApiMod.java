package net.morthen.resource_config_api;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ConfigConstants.MOD_ID)
public class ConfigApiMod {
    public ConfigApiMod(IEventBus eventBus) {
        ConfigConstants.commonInit();
    }
}
