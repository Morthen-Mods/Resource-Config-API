package net.morthen.resourceconfigapi.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.api.ConfigFile;
import net.morthen.resourceconfigapi.api.ConfigRegistry;
import net.morthen.resourceconfigapi.api.ConfigType;
import net.morthen.resourceconfigapi.impl.ModConfigFile;
import net.morthen.resourceconfigapi.impl.platform.ConfigServices;

import java.util.Collection;

/**
 * Syncs the COMMON configs between server and clients. The loader specific entrypoints only have
 * to register {@link ConfigSyncPayload} and forward the events to these methods.
 * <ul>
 *     <li>a joining player receives every COMMON config of the server</li>
 *     <li>changes of operators are applied, saved on the server and sent to every player</li>
 *     <li>changes of other players are rejected, they get the values of the server back</li>
 * </ul>
 */
public final class ConfigNetworking {
    private ConfigNetworking() {}

    // --------------------------------------------------------------------------------------------
    // Server
    // --------------------------------------------------------------------------------------------

    public static void onPlayerJoin(ServerPlayer player) {
        for (ConfigFile config : ConfigRegistry.getAll()) {
            if (config.type() == ConfigType.COMMON) send(player, config);
        }
    }

    /** Handles changes a client sent, has to be called on the server thread. */
    public static void onReceive(ConfigSyncPayload payload, ServerPlayer player) {
        ModConfigFile config = getCommonConfig(payload);
        if (config == null) return;

        MinecraftServer server = player.level().getServer();
        if (!canEdit(server, player)) {
            ConfigConstants.LOGGER.warn("{} tried to change {} without permission", player.getPlainTextName(), config.id());
            send(player, config);
            return;
        }

        config.applyJson(payload.values());
        config.save();
        ConfigConstants.LOGGER.info("{} changed {}: {}", player.getPlainTextName(), config.id(), payload.values());
        server.getPlayerList().getPlayers().forEach(target -> send(target, config));
    }

    public static boolean canEdit(MinecraftServer server, ServerPlayer player) {
        return server.isSingleplayerOwner(player.nameAndId())
                || player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }

    private static void send(ServerPlayer player, ConfigFile config) {
        if (config instanceof ModConfigFile file && ConfigServices.NETWORK.canSendToPlayer(player)) {
            ConfigServices.NETWORK.sendToPlayer(player, new ConfigSyncPayload(file.id(), file.toJson()));
        }
    }

    // --------------------------------------------------------------------------------------------
    // Client
    // --------------------------------------------------------------------------------------------

    /** @return the payload with the given values of the config, as they are stored in the file */
    public static ConfigSyncPayload createPayload(ConfigFile config, Collection<String> keys) {
        return new ConfigSyncPayload(config.id(), ((ModConfigFile) config).toJson(keys));
    }

    /**
     * Applies the values of the server, they are only kept in memory until the config is reloaded.
     * @return the updated config, or null if the payload doesn't belong to a known COMMON config
     */
    public static ConfigFile applyServerValues(ConfigSyncPayload payload) {
        ModConfigFile config = getCommonConfig(payload);
        if (config != null) config.applyServerValues(payload.values());
        return config;
    }

    private static ModConfigFile getCommonConfig(ConfigSyncPayload payload) {
        return ConfigRegistry.get(payload.id())
                .filter(config -> config.type() == ConfigType.COMMON)
                .map(config -> config instanceof ModConfigFile file ? file : null)
                .orElse(null);
    }
}
