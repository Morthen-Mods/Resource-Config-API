package net.morthen.resourceconfigapi.client.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraftforge.network.PacketDistributor;
import net.morthen.resourceconfigapi.ConfigAPI;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;

public class ForgeClientNetworkHelper implements IClientNetworkHelper {
    @Override
    public boolean canSendToServer() {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        return listener != null && ConfigAPI.NETWORK.isRemotePresent(listener.getConnection());
    }

    @Override
    public void sendToServer(ConfigSyncPayload payload) {
        ConfigAPI.NETWORK.send(payload, PacketDistributor.SERVER.noArg());
    }
}
