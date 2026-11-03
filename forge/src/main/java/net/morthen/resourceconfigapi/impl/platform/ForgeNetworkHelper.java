package net.morthen.resourceconfigapi.impl.platform;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.morthen.resourceconfigapi.ConfigAPI;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;

public class ForgeNetworkHelper implements INetworkHelper {
    @Override
    public boolean canSendToPlayer(ServerPlayer player) {
        return ConfigAPI.NETWORK.isRemotePresent(player.connection.getConnection());
    }

    @Override
    public void sendToPlayer(ServerPlayer player, ConfigSyncPayload payload) {
        ConfigAPI.NETWORK.send(payload, PacketDistributor.PLAYER.with(player));
    }
}
