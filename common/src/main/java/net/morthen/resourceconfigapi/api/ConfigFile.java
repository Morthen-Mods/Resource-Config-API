package net.morthen.resourceconfigapi.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

public interface ConfigFile {
    Identifier id();
    String modId();
    String fileName();
    ConfigType type();
    List<ConfigOption<?>> options();
    Optional<ConfigOption<?>> option(String key);
    List<String> categories();
    void save();
    void load();
    void reload();
    Component label();
    Component tooltip();
    Component category(String category);
}
