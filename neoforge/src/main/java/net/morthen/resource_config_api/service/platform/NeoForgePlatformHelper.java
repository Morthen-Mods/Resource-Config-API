package net.morthen.resource_config_api.service.platform;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public Path getConfigDir() {
        return FMLLoader.getCurrent().getGameDir();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDev() {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public String getName() {
        return "NeoForge";
    }
}
