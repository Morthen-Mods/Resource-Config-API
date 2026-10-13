package net.morthen.resource_config_api.service.platform;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.morthen.resource_config_api.ConfigApi;
import net.morthen.resource_config_api.network.ConfigSyncPayload;

public class ForgeNetworkHelper implements INetworkHelper {
    @Override
    public boolean canSendToPlayer(ServerPlayer player) {
        return ConfigApi.CHANNEL.isRemotePresent(player.connection.getConnection());
    }

    @Override
    public void sendToPlayer(ServerPlayer player, ConfigSyncPayload payload) {
        ConfigApi.CHANNEL.send(payload, PacketDistributor.PLAYER.with(player));
    }
}
