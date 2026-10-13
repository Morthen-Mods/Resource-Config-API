package net.morthen.resource_config_api.service.platform;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.morthen.resource_config_api.network.ConfigSyncPayload;

public class FabricClientNetworkHelper implements IClientNetworkHelper {
    @Override
    public boolean canSendToServer() {
        return ClientPlayNetworking.canSend(ConfigSyncPayload.TYPE);
    }

    @Override
    public void sendToServer(ConfigSyncPayload payload) {
        ClientPlayNetworking.send(payload);
    }
}
