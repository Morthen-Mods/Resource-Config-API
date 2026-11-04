package net.morthen.resourceconfigapi.impl.platform;

import net.minecraft.server.level.ServerPlayer;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;
import net.neoforged.neoforge.network.PacketDistributor;

public class ForgeNetworkHelper implements INetworkHelper {
    @Override
    public boolean canSendToPlayer(ServerPlayer player) {
        return player.connection.hasChannel(ConfigSyncPayload.TYPE);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, ConfigSyncPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }
}
