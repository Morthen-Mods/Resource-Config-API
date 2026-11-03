package net.morthen.resourceconfigapi;

import net.minecraft.server.level.ServerPlayer;
import net.morthen.resourceconfigapi.client.ClientConfigSync;
import net.morthen.resourceconfigapi.network.ConfigNetworking;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@Mod(ConfigConstants.MOD_ID)
public class ConfigAPI {
    public ConfigAPI(IEventBus modBus) {
        modBus.addListener(ConfigAPI::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").optional()
                .playBidirectional(ConfigSyncPayload.TYPE, ConfigSyncPayload.CODEC, ConfigAPI::handle, ConfigAPI::handle);
    }

    private static void handle(ConfigSyncPayload payload, IPayloadContext ctx) {
        if (ctx.flow().isServerbound()) {
            ConfigNetworking.onReceive(payload, (ServerPlayer) ctx.player());
        } else {
            ClientConfigSync.onReceive(payload);
        }
    }
}
