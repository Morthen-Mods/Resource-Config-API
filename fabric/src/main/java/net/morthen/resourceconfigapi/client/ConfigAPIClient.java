package net.morthen.resourceconfigapi.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.client.theme.ThemeLoader;
import net.morthen.resourceconfigapi.client.theme.ThemeSettings;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;

public class ConfigAPIClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ThemeSettings.load();
        ThemeLoader themes = new ThemeLoader();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation(ConfigConstants.MOD_ID, "themes");
            }

            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                themes.onResourceManagerReload(manager);
            }
        });
        // the buffer is read on the network thread, the payload is handled on the client thread
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.CHANNEL, (client, handler, buf, sender) -> {
            ConfigSyncPayload payload = ConfigSyncPayload.decode(buf);
            client.execute(() -> ClientConfigSync.onReceive(payload));
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientConfigSync.onDisconnect());
    }
}
