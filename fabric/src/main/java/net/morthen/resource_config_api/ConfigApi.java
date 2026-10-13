package net.morthen.resource_config_api;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.morthen.resource_config_api.network.ConfigNetworking;
import net.morthen.resource_config_api.network.ConfigSyncPayload;

public class ConfigApi implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.clientboundPlay().register(ConfigSyncPayload.TYPE, ConfigSyncPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ConfigSyncPayload.TYPE, ConfigSyncPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.TYPE,
                (payload, context) -> ConfigNetworking.onReceive(payload, context.player()));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ConfigNetworking.onPlayerJoin(handler.player));
    }
}
