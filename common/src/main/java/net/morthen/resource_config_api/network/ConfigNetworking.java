package net.morthen.resource_config_api.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.morthen.resource_config_api.ConfigConstants;
import net.morthen.resource_config_api.api.ConfigRegistry;
import net.morthen.resource_config_api.api.ConfigType;
import net.morthen.resource_config_api.api.ModConfig;
import net.morthen.resource_config_api.service.Services;

public final class ConfigNetworking {
    private ConfigNetworking() {}

    public static void onPlayerJoin(ServerPlayer player) {
        for (ModConfig config : ConfigRegistry.all()) {
            if (config.type() == ConfigType.COMMON) send(player, config);
        }
    }

    public static void onReceive(ConfigSyncPayload payload, ServerPlayer player) {
        ModConfig config = ConfigRegistry.get(payload.id());
        if (config == null || config.type() == ConfigType.CLIENT) return;

        MinecraftServer server = player.level().getServer();
        if (!canEdit(server, player)) {
            ConfigConstants.LOGGER.warn("{} tried to change {} without permission", player.getPlainTextName(), config.id());
            send(player, config);
            return;
        }

        config.apply(payload.values());
        config.save();
        ConfigConstants.LOGGER.info("{} changed {}: {}", player.getPlainTextName(), config.id(), payload.values());
        server.getPlayerList().getPlayers().forEach(target -> send(target, config));
    }

    private static boolean canEdit(MinecraftServer server, ServerPlayer player) {
        return server.isSingleplayerOwner(player.nameAndId())
                || player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }

    private static void send(ServerPlayer player, ModConfig config) {
        if (Services.NETWORK.canSendToPlayer(player)) {
            Services.NETWORK.sendToPlayer(player, new ConfigSyncPayload(config.id(), config.toJson()));
        }
    }
}
