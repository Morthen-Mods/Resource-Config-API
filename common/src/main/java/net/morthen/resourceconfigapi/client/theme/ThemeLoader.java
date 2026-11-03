package net.morthen.resourceconfigapi.client.theme;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.client.ui.Theme;
import net.morthen.resourceconfigapi.client.ui.Themes;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Loads the themes from {@code assets/resourceconfigapi/themes/<id>.json}, so resource packs can add and change them.
 * <pre>
 * {
 *   "parent": "modern",
 *   "cornerRadius": 4,
 *   "accent": "#FF55FFFF"
 * }
 * </pre>
 * Every public field of {@link Theme} can be set by its name. Colors are strings like {@code "#AARRGGBB"}
 * or {@code "#RRGGBB"}, whole numbers are used as they are and {@code tooltipStyle} is an identifier.
 * Fields that aren't set come from the {@code parent} theme, or from the defaults of {@link Theme}
 * (the vanilla look) without one. The id of a theme is its file name, the dropdown shows the
 * translation {@code config.resourceconfigapi.screen.theme.<id>} or the id itself.
 */
public class ThemeLoader implements ResourceManagerReloadListener {
    private static final FileToIdConverter FILES = FileToIdConverter.json("themes");
    private static final Map<String, Field> FIELDS = new HashMap<>();

    static {
        for (Field field : Theme.class.getFields()) {
            if (!Modifier.isStatic(field.getModifiers())) FIELDS.put(field.getName(), field);
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        Map<String, JsonObject> files = new TreeMap<>();
        for (Map.Entry<Identifier, Resource> entry : FILES.listMatchingResources(manager).entrySet()) {
            if (!entry.getKey().getNamespace().equals(ConfigConstants.MOD_ID)) continue;

            String id = FILES.fileToId(entry.getKey()).getPath();
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                files.put(id, JsonParser.parseReader(reader).getAsJsonObject());
            } catch (Exception e) {
                ConfigConstants.LOGGER.warn("Could not read the theme {}", entry.getKey(), e);
            }
        }

        Map<String, Theme> themes = new LinkedHashMap<>();
        for (String id : files.keySet()) {
            Theme theme = new Theme();
            apply(theme, id, files, new HashSet<>());
            themes.put(id, theme);
        }

        Themes.setLoaded(themes);
        ThemeSettings.reselect();
    }

    /** Applies the parents first, so the values of the theme itself win. */
    private static void apply(Theme theme, String id, Map<String, JsonObject> files, Set<String> chain) {
        JsonObject json = files.get(id);
        if (json == null) {
            // the defaults of Theme are the vanilla look, it doesn't need a file
            if (!id.equals(Themes.VANILLA)) ConfigConstants.LOGGER.warn("Unknown parent theme {}", id);
            return;
        }
        if (!chain.add(id)) {
            ConfigConstants.LOGGER.warn("The theme {} is its own parent", id);
            return;
        }

        if (json.has("parent")) apply(theme, json.get("parent").getAsString(), files, chain);

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!entry.getKey().equals("parent")) set(theme, id, entry.getKey(), entry.getValue());
        }
    }

    private static void set(Theme theme, String id, String name, JsonElement value) {
        Field field = FIELDS.get(name);
        if (field == null) {
            ConfigConstants.LOGGER.warn("Theme {} has the unknown field {}", id, name);
            return;
        }

        try {
            if (field.getType() == int.class) {
                field.setInt(theme, parseInt(value.getAsString()));
            } else if (field.getType() == Identifier.class) {
                Identifier identifier = Identifier.tryParse(value.getAsString());
                if (identifier == null) throw new IllegalArgumentException("not an identifier");
                field.set(theme, identifier);
            }
        } catch (Exception e) {
            ConfigConstants.LOGGER.warn("Theme {} has an invalid value for {}: {}", id, name, value, e);
        }
    }

    /** Parses {@code #AARRGGBB}, {@code #RRGGBB} (opaque) or a whole number. */
    private static int parseInt(String text) {
        if (!text.startsWith("#")) return Integer.parseInt(text);

        String hex = text.substring(1);
        return switch (hex.length()) {
            case 6 -> 0xFF000000 | Integer.parseInt(hex, 16);
            case 8 -> (int) Long.parseLong(hex, 16);
            default -> throw new IllegalArgumentException("a color needs 6 or 8 hex digits");
        };
    }
}
