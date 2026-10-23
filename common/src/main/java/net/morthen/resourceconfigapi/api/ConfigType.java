package net.morthen.resourceconfigapi.api;

import net.minecraft.network.chat.Component;

import java.util.Locale;

public enum ConfigType {
    // Configs never get synced or loaded by the server
    CLIENT("client"),
    // Config get synced and loaded by the server and are editable by server admins
    COMMON("common");

    final Component component;

    ConfigType(String key) {
        component = Component.translatable("resource_config_api.base.config_type." + key);
    }

    public Component getComponent() {
        return component;
    }

    public String getDirectory() {
        return name().toLowerCase(Locale.ROOT);
    }
}
