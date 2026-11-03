package net.morthen.resourceconfigapi.client.theme;

import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.client.ui.Themes;
import net.morthen.resourceconfigapi.impl.platform.ConfigServices;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Remembers the selected UI theme between launches. */
public final class ThemeSettings {
    private static String saved = Themes.VANILLA;

    private ThemeSettings() {}

    private static Path file() {
        return ConfigServices.PLATFORM.getConfigDir().resolve("resourceconfigapi_theme.txt");
    }

    public static void load() {
        try {
            if (Files.exists(file())) saved = Files.readString(file()).trim();
        } catch (IOException e) {
            ConfigConstants.LOGGER.warn("Could not read the theme setting", e);
        }
        Themes.select(saved);
    }

    /**
     * Selects the saved theme again after the available themes changed. Themes from resource packs
     * aren't there at startup, and the saved one is kept if its pack is gone for now.
     */
    public static void reselect() {
        if (!Themes.select(saved)) Themes.select(Themes.VANILLA);
    }

    /** Selects the theme and writes it to disk. */
    public static void select(String id) {
        if (!Themes.select(id)) return;

        saved = id;
        try {
            Files.writeString(file(), id);
        } catch (IOException e) {
            ConfigConstants.LOGGER.warn("Could not save the theme setting", e);
        }
    }
}
