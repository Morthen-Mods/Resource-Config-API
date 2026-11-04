package net.morthen.resourceconfigapi;

import net.minecraft.server.level.ServerPlayer;
import net.morthen.resourceconfigapi.client.ClientConfigSync;
import net.morthen.resourceconfigapi.network.ConfigNetworking;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@Mod(ConfigConstants.MOD_ID)
public class ConfigAPI {
    public ConfigAPI(IEventBus modBus) {
        modBus.addListener(ConfigAPI::registerPayloads);
    }

    // the handlers run on the main thread, the client one is only called on the client
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").optional()
                .playBidirectional(ConfigSyncPayload.TYPE, ConfigSyncPayload.CODEC,
                        new DirectionalPayloadHandler<>(ConfigAPI::handleClient, ConfigAPI::handleServer));
    }

    private static void handleClient(ConfigSyncPayload payload, IPayloadContext ctx) {
        ClientConfigSync.onReceive(payload);
    }

    private static void handleServer(ConfigSyncPayload payload, IPayloadContext ctx) {
        ConfigNetworking.onReceive(payload, (ServerPlayer) ctx.player());
    }
}
