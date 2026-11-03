package net.morthen.resourceconfigapi.impl.platform;

import net.minecraft.server.level.ServerPlayer;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;

/** Server side networking of the loader. */
public interface INetworkHelper {
    /** @return true if the player has the mod installed and can receive the sync payload */
    boolean canSendToPlayer(ServerPlayer player);

    void sendToPlayer(ServerPlayer player, ConfigSyncPayload payload);
}
