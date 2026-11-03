package net.morthen.resourceconfigapi.client;

import net.minecraft.client.Minecraft;
import net.minecraft.server.permissions.Permissions;
import net.morthen.resourceconfigapi.api.ConfigFile;
import net.morthen.resourceconfigapi.api.ConfigRegistry;
import net.morthen.resourceconfigapi.api.ConfigType;
import net.morthen.resourceconfigapi.client.platform.ClientServices;
import net.morthen.resourceconfigapi.client.screen.ConfigScreen;
import net.morthen.resourceconfigapi.network.ConfigNetworking;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;

import java.util.List;

/** Client side of the COMMON config sync. */
public final class ClientConfigSync {
    private static boolean holdsServerValues;

    private ClientConfigSync() {}

    /** Values of the server arrived, has to be called on the client thread. */
    public static void onReceive(ConfigSyncPayload payload) {
        Minecraft client = Minecraft.getInstance();
        // the integrated server already shares the config fields with us
        if (client.hasSingleplayerServer()) return;

        Runnable apply = () -> {
            if (ConfigNetworking.applyServerValues(payload) != null) holdsServerValues = true;
        };

        // an open config screen keeps the edits of the user, the other entries show the new values
        if (client.gui.screen() instanceof ConfigScreen screen) {
            ConfigRegistry.get(payload.id()).ifPresentOrElse(config -> screen.applyExternalChange(config, apply), apply);
        } else {
            apply.run();
        }
    }

    /** Restores the values of the local files, can be called on any thread. */
    public static void onDisconnect() {
        Minecraft.getInstance().execute(() -> {
            if (!holdsServerValues) return;

            ConfigRegistry.getAll().stream()
                    .filter(config -> config.type() == ConfigType.COMMON)
                    .forEach(ConfigFile::reload);
            holdsServerValues = false;
        });
    }

    /** @return true if the player may change configs of this type, COMMON configs of a server need operator rights */
    public static boolean canEdit(ConfigType type) {
        Minecraft client = Minecraft.getInstance();
        return type == ConfigType.CLIENT || !isConnectedToSyncingServer() || client.hasSingleplayerServer()
                || client.player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }

    /** @return true if the values of COMMON configs come from the server */
    public static boolean usesServerValues(ConfigType type) {
        return type == ConfigType.COMMON && isConnectedToSyncingServer();
    }

    /**
     * Saves the config after the given options were changed. COMMON configs are sent to the server while
     * connected to one, the server applies and saves them and sends the result back to every player.
     */
    public static void submit(ConfigFile config, List<String> changedKeys) {
        if (usesServerValues(config.type())) {
            ClientServices.NETWORK.sendToServer(ConfigNetworking.createPayload(config, changedKeys));
        } else {
            config.save();
        }
    }

    private static boolean isConnectedToSyncingServer() {
        return Minecraft.getInstance().player != null && ClientServices.NETWORK.canSendToServer();
    }
}
