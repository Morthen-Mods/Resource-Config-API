package net.morthen.resourceconfigapi.impl.platform;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getServerConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isDev() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
