package net.morthen.resourceconfigapi.client;

import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.api.ConfigRegistry;
import net.morthen.resourceconfigapi.client.screen.ConfigScreen;
import net.morthen.resourceconfigapi.client.theme.ThemeLoader;
import net.morthen.resourceconfigapi.client.theme.ThemeSettings;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.Optional;

@EventBusSubscriber(modid = ConfigConstants.MOD_ID, value = Dist.CLIENT)
public class ModClientHandler {
    @SubscribeEvent
    public static void registerConfigScreens(FMLClientSetupEvent event) {
        event.enqueueWork(ThemeSettings::load);

        ConfigRegistry.getRegisteredMods().forEach(modId -> {
            Optional<? extends ModContainer> optional = ModList.get().getModContainerById(modId);

            optional.ifPresent(container -> {
                container.registerExtensionPoint(IConfigScreenFactory.class,
                        (_, screen) -> new ConfigScreen(screen, modId));
            });
        });
    }

    @SubscribeEvent
    public static void registerReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(ConfigConstants.of("themes"), new ThemeLoader());
    }
}
