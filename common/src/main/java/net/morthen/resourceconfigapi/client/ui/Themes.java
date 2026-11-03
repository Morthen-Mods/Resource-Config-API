package net.morthen.resourceconfigapi.client.ui;

import java.util.*;

/**
 * Registry of the available {@link Theme}s and the one that is currently used by all widgets.
 * Themes from code are added with {@link #register}, the ones from resource packs are
 * replaced as a whole on every resource reload, see {@code ThemeLoader}.
 * <pre>
 * Themes.register("mine", new MyTheme());
 * Themes.select("mine");
 * </pre>
 */
public final class Themes {
    public static final String VANILLA = "vanilla";

    private static final Map<String, Theme> THEMES = new LinkedHashMap<>();
    private static volatile Map<String, Theme> loaded = Map.of();
    private static Theme current;
    private static String currentId;

    static {
        register(VANILLA, new Theme());
        select(VANILLA);
    }

    private Themes() {}

    /** The theme every widget draws with. */
    public static Theme get() {
        return current;
    }

    public static String currentId() {
        return currentId;
    }

    /** Adds a theme or replaces the one with the same id. */
    public static void register(String id, Theme theme) {
        THEMES.put(id, theme);
    }

    /** Replaces the themes loaded from resource packs, they win over registered ones with the same id. */
    public static void setLoaded(Map<String, Theme> themes) {
        loaded = Collections.unmodifiableMap(new LinkedHashMap<>(themes));
    }

    /** Switches the active theme, returns false and changes nothing if the id is unknown. */
    public static boolean select(String id) {
        Theme theme = loaded.getOrDefault(id, THEMES.get(id));
        if (theme == null) return false;

        current = theme;
        currentId = id;
        return true;
    }

    public static Set<String> ids() {
        Set<String> ids = new LinkedHashSet<>(THEMES.keySet());
        ids.addAll(loaded.keySet());
        return Collections.unmodifiableSet(ids);
    }
}
