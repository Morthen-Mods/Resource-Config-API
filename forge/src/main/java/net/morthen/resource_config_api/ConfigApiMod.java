package net.morthen.resource_config_api;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CommonConstants.MOD_ID)
public class ConfigApiMod {
    public ConfigApiMod(FMLJavaModLoadingContext context) {
        CommonConstants.commonInit();
        CommonConstants.LOGGER.info("Loading ConfigApiMod");
    }
}
