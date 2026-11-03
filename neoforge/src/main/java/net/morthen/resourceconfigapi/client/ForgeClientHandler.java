package net.morthen.resourceconfigapi.client;

import net.morthen.resourceconfigapi.ConfigConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = ConfigConstants.MOD_ID, value = Dist.CLIENT)
public class ForgeClientHandler {
    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientConfigSync.onDisconnect();
    }
}
