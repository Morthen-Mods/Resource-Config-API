package net.morthen.resourceconfigapi.impl;

import net.morthen.resourceconfigapi.api.*;
import net.morthen.resourceconfigapi.api.annotations.ConfigEntry;

import java.lang.reflect.Field;
import java.util.Optional;

public class FieldConfigOption<T> implements ConfigOption<T> {

    static FieldConfigOption<?> create(ModConfigFile file, Field field, ConfigEntry entry) {
        return null;
    }


    @Override
    public T get() {
        return null;
    }

    @Override
    public void set(T value) {

    }

    @Override
    public T defaultValue() {
        return null;
    }

    @Override
    public ConfigFile file() {
        return null;
    }

    @Override
    public String key() {
        return "";
    }

    @Override
    public Class<?> type() {
        return null;
    }

    @Override
    public String category() {
        return "";
    }

    @Override
    public Optional<Range> range() {
        return Optional.empty();
    }

    @Override
    public RestartRequirement restartRequirement() {
        return null;
    }

    @Override
    public ColorMode colorMode() {
        return null;
    }

    @Override
    public String labelKey() {
        return "";
    }

    @Override
    public String tooltipKey() {
        return "";
    }
}
