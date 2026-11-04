package net.morthen.resourceconfigapi.impl;

import net.minecraft.server.level.ServerPlayer;
import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.network.ConfigNetworking;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = ConfigConstants.MOD_ID)
public class ForgeHandler {
    @SubscribeEvent
    public static void registerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ConfigNetworking.onPlayerJoin(player);
        }
    }
}
