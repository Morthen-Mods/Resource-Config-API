package net.morthen.resource_config_api.service.platform;

import java.nio.file.Path;

public interface IPlatformHelper {
    Path getConfigDir();

    boolean isModLoaded(String modId);

    boolean isDev();

    String getName();
}
