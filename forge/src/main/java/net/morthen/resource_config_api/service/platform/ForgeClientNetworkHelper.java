package net.morthen.resource_config_api.service.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraftforge.network.PacketDistributor;
import net.morthen.resource_config_api.ConfigApi;
import net.morthen.resource_config_api.network.ConfigSyncPayload;

public class ForgeClientNetworkHelper implements IClientNetworkHelper {
    @Override
    public boolean canSendToServer() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        return connection != null && ConfigApi.CHANNEL.isRemotePresent(connection.getConnection());
    }

    @Override
    public void sendToServer(ConfigSyncPayload payload) {
        ConfigApi.CHANNEL.send(payload, PacketDistributor.SERVER.noArg());
    }
}
