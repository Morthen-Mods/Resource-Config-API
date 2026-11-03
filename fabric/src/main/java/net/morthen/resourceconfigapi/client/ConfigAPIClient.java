package net.morthen.resourceconfigapi.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.client.theme.ThemeLoader;
import net.morthen.resourceconfigapi.client.theme.ThemeSettings;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;

public class ConfigAPIClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ThemeSettings.load();
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
                Identifier.fromNamespaceAndPath(ConfigConstants.MOD_ID, "themes"), new ThemeLoader());
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.TYPE, (payload, context) -> ClientConfigSync.onReceive(payload));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientConfigSync.onDisconnect());
    }
}
