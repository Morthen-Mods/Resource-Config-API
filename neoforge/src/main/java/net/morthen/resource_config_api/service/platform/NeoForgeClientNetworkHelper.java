package net.morthen.resource_config_api.service.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.morthen.resource_config_api.network.ConfigSyncPayload;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class NeoForgeClientNetworkHelper implements IClientNetworkHelper {
    @Override
    public boolean canSendToServer() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        return connection != null && connection.hasChannel(ConfigSyncPayload.TYPE);
    }

    @Override
    public void sendToServer(ConfigSyncPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }
}
