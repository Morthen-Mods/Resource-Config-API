package net.morthen.resource_config_api;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.morthen.resource_config_api.api.ConfigRegistry;
import net.morthen.resource_config_api.api.ModConfig;
import net.morthen.resource_config_api.client.ClientConfigSync;
import net.morthen.resource_config_api.client.screen.ConfigScreen;
import net.morthen.resource_config_api.network.ConfigNetworking;
import net.morthen.resource_config_api.network.ConfigSyncPayload;

import java.util.Optional;

@Mod(ConfigConstants.MOD_ID)
public class ConfigApi {
    public static final Channel<CustomPacketPayload> CHANNEL = ChannelBuilder.named(ConfigConstants.of("main"))
            .optional().payloadChannel().play().bidirectional().addMain(ConfigSyncPayload.TYPE, ConfigSyncPayload.CODEC.cast(), ConfigApi::handle).build();

    private static void handle(ConfigSyncPayload payload, CustomPayloadEvent.Context ctx) {
        if (ctx.isServerSide()) ConfigNetworking.onReceive(payload, ctx.getSender());
        else ClientConfigSync.onReceive(payload);
    }

    @Mod.EventBusSubscriber(modid = ConfigConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeClientEvents {
        @SubscribeEvent
        public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
            ClientConfigSync.onDisconnect();
        }
    }

    @Mod.EventBusSubscriber(modid = ConfigConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) ConfigNetworking.onPlayerJoin(player);
        }
    }

    @Mod.EventBusSubscriber(modid = ConfigConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ConfigApiClientEvents {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            ConfigRegistry.all().forEach(config -> {
                Optional<? extends ModContainer> modContainer = ModList.getModContainerById(config.modId());

                modContainer.ifPresent(mod -> {
                    if (mod.getCustomExtension(ConfigScreenHandler.ConfigScreenFactory.class).isPresent()) return;

                    mod.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                            () -> new ConfigScreenHandler.ConfigScreenFactory((_, parent) -> new ConfigScreen(parent, config.modId())));
                });
            });
        }
    }
}
