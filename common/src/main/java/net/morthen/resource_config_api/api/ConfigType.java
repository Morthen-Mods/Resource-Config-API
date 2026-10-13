package net.morthen.resource_config_api.api;

import java.util.Locale;

public enum ConfigType {
    /** Only exists on the client and is never synced. */
    CLIENT,
    /** Exists on both sides. The server is authoritative and syncs its values to every client. */
    COMMON;

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
