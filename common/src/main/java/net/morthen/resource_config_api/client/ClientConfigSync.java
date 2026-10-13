package net.morthen.resource_config_api.client;

import net.minecraft.client.Minecraft;
import net.minecraft.server.permissions.Permissions;
import net.morthen.resource_config_api.api.ConfigRegistry;
import net.morthen.resource_config_api.api.ConfigType;
import net.morthen.resource_config_api.api.ModConfig;
import net.morthen.resource_config_api.network.ConfigSyncPayload;

import java.util.Map;

public final class ClientConfigSync {
    private static boolean holdsServerValues;

    private ClientConfigSync() {}

    public static void onReceive(ConfigSyncPayload payload) {
        ModConfig config = ConfigRegistry.get(payload.id());
        if (config == null || config.type() != ConfigType.COMMON) return;

        Minecraft client = Minecraft.getInstance();
        // the integrated server already shares the config fields with us
        if (!client.hasSingleplayerServer()) {
            config.apply(payload.values());
            holdsServerValues = true;
        }
    }

    public static void onDisconnect() {
        // some loaders fire this on the network thread
        Minecraft.getInstance().execute(() -> {
            if (!holdsServerValues) return;

            ConfigRegistry.all().stream()
                    .filter(config -> config.type() == ConfigType.COMMON)
                    .forEach(ModConfig::load);
            holdsServerValues = false;
        });
    }

    public static boolean canEdit(ConfigType type) {
        Minecraft client = Minecraft.getInstance();
        return type == ConfigType.CLIENT || !isConnectedToSyncingServer() || client.hasSingleplayerServer()
                || client.player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }

    public static void submit(ModConfig config, Map<String, Object> changes) {
        if (config.type() == ConfigType.COMMON && isConnectedToSyncingServer()) {
            ClientServices.NETWORK.sendToServer(new ConfigSyncPayload(config.id(), ModConfig.toJson(changes)));
            return;
        }

        changes.forEach(config::set);
        config.save();
    }

    private static boolean isConnectedToSyncingServer() {
        return Minecraft.getInstance().player != null && ClientServices.NETWORK.canSendToServer();
    }
}
