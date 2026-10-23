package net.morthen.resourceconfigapi.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.morthen.resourceconfigapi.api.ConfigFile;
import net.morthen.resourceconfigapi.api.ConfigOption;
import net.morthen.resourceconfigapi.api.ConfigType;
import net.morthen.resourceconfigapi.api.annotations.Config;

import java.util.List;
import java.util.Optional;

public final class ModConfigFile implements ConfigFile {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ModConfigFile(Class<?> clazz, Config config, Identifier id, String modId) {

    }

    public Gson gson() {
        return gson;
    }

    @Override
    public void save() {

    }

    @Override
    public void load() {

    }

    @Override
    public void reload() {

    }

    @Override
    public Identifier id() {
        return null;
    }

    @Override
    public String modId() {
        return "";
    }

    @Override
    public String fileName() {
        return "";
    }

    @Override
    public ConfigType type() {
        return null;
    }

    @Override
    public List<ConfigOption<?>> options() {
        return List.of();
    }

    @Override
    public Optional<ConfigOption<?>> option(String key) {
        return Optional.empty();
    }

    @Override
    public List<String> categories() {
        return List.of();
    }

    @Override
    public Component label() {
        return null;
    }

    @Override
    public Component tooltip() {
        return null;
    }

    @Override
    public Component category(String category) {
        return null;
    }
}
