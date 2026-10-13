package net.morthen.resource_config_api.service.platform;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isDedicatedServer() {
        return FMLEnvironment.getDist() == Dist.DEDICATED_SERVER;
    }

    @Override
    public String getModName(String modId) {
        return ModList.get().getModContainerById(modId)
                .map(mod -> mod.getModInfo().getDisplayName())
                .orElse(modId);
    }
}
