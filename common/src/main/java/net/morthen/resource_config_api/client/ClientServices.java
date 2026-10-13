package net.morthen.resource_config_api.client;


import net.morthen.resource_config_api.service.Services;
import net.morthen.resource_config_api.service.platform.IClientNetworkHelper;

// kept apart from Services so a dedicated server never loads client implementations
public class ClientServices {
    public static final IClientNetworkHelper NETWORK = Services.load(IClientNetworkHelper.class);
}
