package net.morthen.resource_config_api.service.platform;

import net.minecraft.server.level.ServerPlayer;
import net.morthen.resource_config_api.network.ConfigSyncPayload;

public interface INetworkHelper {
    boolean canSendToPlayer(ServerPlayer player);

    void sendToPlayer(ServerPlayer player, ConfigSyncPayload payload);
}
