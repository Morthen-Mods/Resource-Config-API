package net.morthen.resourceconfigapi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.morthen.resourceconfigapi.network.ConfigNetworking;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;

public class ConfigAPI implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(ConfigSyncPayload.TYPE, ConfigSyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ConfigSyncPayload.TYPE, ConfigSyncPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.TYPE, (payload, context) -> ConfigNetworking.onReceive(payload, context.player()));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ConfigNetworking.onPlayerJoin(handler.player));
    }
}
