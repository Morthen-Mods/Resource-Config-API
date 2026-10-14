package net.morthen.resource_config_api.testmod;

import net.morthen.resource_config_api.ConfigConstants;
import net.morthen.resource_config_api.api.ConfigRegistry;import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestmodConstants {
    public static final String MOD_ID = ConfigConstants.MOD_ID + "_testmod";
    public static final String MOD_NAME = "Resource-Config-Api Test Mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static void commonInit() {
        ConfigRegistry.register(TestConfigs.ClientConfig.class, MOD_ID);
        ConfigRegistry.register(TestConfigs.CommonConfig.class, MOD_ID);
    }
}
