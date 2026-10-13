package net.morthen.resource_config_api;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigConstants {
    public static final String MOD_ID = "resource_config_api";
    public static final String MOD_NAME = "Resource-Config-Api";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier of(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void commonInit() {
    }
}
