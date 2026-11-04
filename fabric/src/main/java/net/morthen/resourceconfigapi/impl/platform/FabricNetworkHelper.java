package net.morthen.resourceconfigapi.impl.platform;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;

public class FabricNetworkHelper implements INetworkHelper {
    @Override
    public boolean canSendToPlayer(ServerPlayer player) {
        return ServerPlayNetworking.canSend(player, ConfigSyncPayload.CHANNEL);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, ConfigSyncPayload payload) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        payload.encode(buf);
        ServerPlayNetworking.send(player, ConfigSyncPayload.CHANNEL, buf);
    }
}
