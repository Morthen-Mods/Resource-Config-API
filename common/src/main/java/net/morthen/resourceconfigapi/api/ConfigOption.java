package net.morthen.resourceconfigapi.api;

import java.util.Optional;

public interface ConfigOption<T> {
    ConfigFile file();
    String key();
    Class<?> type();
    String category();
    T get();
    void set(T value);
    T defaultValue();
    Optional<Range> range();
    RestartRequirement restartRequirement();
    ColorMode colorMode();
    String labelKey();
    String tooltipKey();
}
