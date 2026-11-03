package net.morthen.resourceconfigapi.client.platform;

import net.morthen.resourceconfigapi.network.ConfigSyncPayload;

/** Client side networking of the loader. */
public interface IClientNetworkHelper {
    /** @return true if the connected server has the mod installed and can receive the sync payload */
    boolean canSendToServer();

    void sendToServer(ConfigSyncPayload payload);
}
