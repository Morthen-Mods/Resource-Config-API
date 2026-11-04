package net.morthen.resourceconfigapi.impl.platform;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;

public class FabricNetworkHelper implements INetworkHelper {
    @Override
    public boolean canSendToPlayer(ServerPlayer player) {
        return ServerPlayNetworking.canSend(player, ConfigSyncPayload.TYPE);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, ConfigSyncPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }
}
