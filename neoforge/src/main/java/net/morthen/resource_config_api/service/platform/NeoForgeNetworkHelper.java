package net.morthen.resource_config_api.service.platform;

import net.minecraft.server.level.ServerPlayer;
import net.morthen.resource_config_api.network.ConfigSyncPayload;
import net.neoforged.neoforge.network.PacketDistributor;

public class NeoForgeNetworkHelper implements INetworkHelper {
    @Override
    public boolean canSendToPlayer(ServerPlayer player) {
        return player.connection.hasChannel(ConfigSyncPayload.TYPE);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, ConfigSyncPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }
}
