package net.morthen.resource_config_api.api;

import net.minecraft.network.chat.Component;
import net.morthen.resource_config_api.ConfigConstants;

import java.util.Locale;

public enum ConfigType {
    /** Only exists on the client and is never synced. */
    CLIENT(ConfigConstants.TAB_CLIENT),
    /** Exists on both sides. The server is authoritative and syncs its values to every client. */
    COMMON(ConfigConstants.TAB_COMMON);

    private final Component label;

    ConfigType(Component label) {
        this.label = label;
    }

    public Component getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
