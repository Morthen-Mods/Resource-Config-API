package net.morthen.resource_config_api;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.morthen.resource_config_api.client.ClientConfigSync;
import net.morthen.resource_config_api.network.ConfigSyncPayload;

public class ConfigApiClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.TYPE, (payload, context) -> ClientConfigSync.onReceive(payload));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientConfigSync.onDisconnect());
    }
}
