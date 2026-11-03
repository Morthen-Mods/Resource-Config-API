package net.morthen.resourceconfigapi.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.api.ConfigRegistry;
import net.morthen.resourceconfigapi.client.screen.ConfigScreen;
import net.morthen.resourceconfigapi.client.theme.ThemeSettings;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = ConfigConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientHandler {
    @SubscribeEvent
    public static void registerConfigScreens(FMLClientSetupEvent event) {
        // parallel dispatch event, the theme is selected on the main thread before the first resource reload applies
        event.enqueueWork(ThemeSettings::load);

        ConfigRegistry.getRegisteredMods().forEach(modId -> {
            Optional<? extends ModContainer> optional = ModList.getModContainerById(modId);

            optional.ifPresent(container -> {
                container.registerExtensionPoint(
                        ConfigScreenHandler.ConfigScreenFactory.class,
                        () -> new ConfigScreenHandler.ConfigScreenFactory(
                                (_, screen) -> new ConfigScreen(screen, modId))
                );
            });
        });
    }
}
