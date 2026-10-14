package net.morthen.resource_config_api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigConstants {
    public static final String MOD_ID = "resource_config_api";
    public static final String MOD_NAME = "Resource-Config-Api";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    // Components
    public static final Component CATEGORY_GENERAL = create("category.general");
    public static final Component TAB_CLIENT = create("tab.client");
    public static final Component TAB_COMMON = create("tab.common");
    public static final Component RESET = create("reset");
    public static final Component READ_ONLY = create("read_only");
    public static final Component DIRECT_IMPACT = create("direct_impact");
    public static final Component WORLD_RESTART = create("world_restart");
    public static final Component GAME_RESTART = create("game_restart");
    public static final Component SAVE_AND_CLOSE = create("save_and_close");

    public static Identifier of(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private static Component create(String key) {
        return Component.translatable(MOD_ID + "." + key);
    }
}
