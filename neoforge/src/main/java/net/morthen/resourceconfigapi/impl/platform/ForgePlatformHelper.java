package net.morthen.resourceconfigapi.impl.platform;

import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }
}
