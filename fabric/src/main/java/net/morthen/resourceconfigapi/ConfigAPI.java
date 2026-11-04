package net.morthen.resourceconfigapi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.morthen.resourceconfigapi.network.ConfigNetworking;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;

public class ConfigAPI implements ModInitializer {
    @Override
    public void onInitialize() {
        // the buffer is read on the network thread, the payload is handled on the server thread
        ServerPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.CHANNEL, (server, player, handler, buf, sender) -> {
            ConfigSyncPayload payload = ConfigSyncPayload.decode(buf);
            server.execute(() -> ConfigNetworking.onReceive(payload, player));
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ConfigNetworking.onPlayerJoin(handler.player));
        // in 1.20.1 the client may register its channels after the join, the configs are sent as soon as it can receive them
        S2CPlayChannelEvents.REGISTER.register((handler, sender, server, channels) -> {
            if (channels.contains(ConfigSyncPayload.CHANNEL)) server.execute(() -> ConfigNetworking.onPlayerJoin(handler.player));
        });
    }
}
