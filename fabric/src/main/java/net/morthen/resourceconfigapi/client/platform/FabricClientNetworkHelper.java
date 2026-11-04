package net.morthen.resourceconfigapi.client.platform;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;

public class FabricClientNetworkHelper implements IClientNetworkHelper {
    @Override
    public boolean canSendToServer() {
        return ClientPlayNetworking.canSend(ConfigSyncPayload.CHANNEL);
    }

    @Override
    public void sendToServer(ConfigSyncPayload payload) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        payload.encode(buf);
        ClientPlayNetworking.send(ConfigSyncPayload.CHANNEL, buf);
    }
}
