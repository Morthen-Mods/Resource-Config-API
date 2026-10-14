package net.morthen.resource_config_api;

import net.minecraft.server.level.ServerPlayer;
import net.morthen.resource_config_api.api.ConfigRegistry;
import net.morthen.resource_config_api.api.ModConfig;
import net.morthen.resource_config_api.client.ClientConfigSync;
import net.morthen.resource_config_api.client.screen.ConfigScreen;
import net.morthen.resource_config_api.network.ConfigNetworking;
import net.morthen.resource_config_api.network.ConfigSyncPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Optional;

@Mod(ConfigConstants.MOD_ID)
public class ConfigApi {

    @EventBusSubscriber(modid = ConfigConstants.MOD_ID)
    public static class ConfigApiEvents {
        @SubscribeEvent
        public static void registerPayloads(RegisterPayloadHandlersEvent event) {
            PayloadRegistrar registrar = event.registrar("1").optional();

            registrar.playBidirectional(ConfigSyncPayload.TYPE, ConfigSyncPayload.CODEC,
                    (payload, context) -> ConfigNetworking.onReceive(payload, (ServerPlayer) context.player()));
        }

        @SubscribeEvent
        public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                ConfigNetworking.onPlayerJoin(player);
            }
        }
    }

    @EventBusSubscriber(modid = ConfigConstants.MOD_ID, value = Dist.CLIENT)
    public static class ConfigApiClientEvents {
        @SubscribeEvent
        public static void registerClientPayloads(RegisterClientPayloadHandlersEvent event) {
            event.register(ConfigSyncPayload.TYPE, (payload, context) -> ClientConfigSync.onReceive(payload));
        }

        @SubscribeEvent
        public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
            ClientConfigSync.onDisconnect();
        }

        @SubscribeEvent
        public static void commonSetup(FMLClientSetupEvent event) {
            ConfigRegistry.all().forEach(config -> {
                Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(config.modId());

                modContainer.ifPresent(mod -> {
                    if (mod.getCustomExtension(IConfigScreenFactory.class).isPresent()) return;

                    mod.registerExtensionPoint(IConfigScreenFactory.class, (_, parent) -> new ConfigScreen(parent, config.modId()));
                });
            });
        }
    }
}
