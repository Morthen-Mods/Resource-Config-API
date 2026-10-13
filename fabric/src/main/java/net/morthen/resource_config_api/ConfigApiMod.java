package net.morthen.resource_config_api;

import net.fabricmc.api.ModInitializer;

public class ConfigApiMod implements ModInitializer {
    @Override
    public void onInitialize() {
        CommonConstants.commonInit();
        CommonConstants.LOGGER.info("Loading ConfigApiMod");
    }
}
