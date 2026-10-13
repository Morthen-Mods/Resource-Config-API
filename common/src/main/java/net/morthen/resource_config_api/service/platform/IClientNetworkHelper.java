package net.morthen.resource_config_api.service.platform;

import net.morthen.resource_config_api.network.ConfigSyncPayload;

public interface IClientNetworkHelper {
    boolean canSendToServer();

    void sendToServer(ConfigSyncPayload payload);
}
