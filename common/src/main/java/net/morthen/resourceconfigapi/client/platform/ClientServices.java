package net.morthen.resourceconfigapi.client.platform;

import java.util.ServiceLoader;

/** Client only services, kept apart from the common ones so a dedicated server never loads them. */
public final class ClientServices {
    public static final IClientNetworkHelper NETWORK = load(IClientNetworkHelper.class);

    private ClientServices() {}

    private static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Failed to load service for " + clazz.getName()));
    }
}
