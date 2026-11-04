package net.morthen.resourceconfigapi.client.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;
import net.neoforged.neoforge.network.PacketDistributor;

public class ForgeClientNetworkHelper implements IClientNetworkHelper {
    @Override
    public boolean canSendToServer() {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        return listener != null && listener.hasChannel(ConfigSyncPayload.TYPE);
    }

    @Override
    public void sendToServer(ConfigSyncPayload payload) {
        PacketDistributor.sendToServer(payload);
    }
}
