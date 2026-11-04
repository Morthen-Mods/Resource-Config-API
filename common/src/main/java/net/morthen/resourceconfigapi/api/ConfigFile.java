package net.morthen.resourceconfigapi.api;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

/** A registered config, one json file with its options. */
public interface ConfigFile {

    /** @return {@code {modId}:{type}/{fileName}} */
    ResourceLocation id();

    String modId();

    String fileName();

    ConfigType type();

    /** @return all options in declaration order */
    List<ConfigOption<?>> options();

    Optional<ConfigOption<?>> option(String key);

    /** @return the categories in the order of their first option, without the empty category */
    List<String> categories();

    /** Writes the current values to the file. */
    void save();

    /** Reads the file again and applies the values. */
    void reload();

    String labelKey();

    String tooltipKey();

    String categoryLabelKey(String category);
}
