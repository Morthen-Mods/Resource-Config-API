package net.morthen.resourceconfigapi;

import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import net.morthen.resourceconfigapi.client.ClientConfigSync;
import net.morthen.resourceconfigapi.network.ConfigNetworking;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;

@Mod(ConfigConstants.MOD_ID)
public class ConfigAPI {
    public static SimpleChannel NETWORK = ChannelBuilder.named(ConfigConstants.of("main"))
            .networkProtocolVersion(1).optional().simpleChannel().play()
            .bidirectional().addMain(ConfigSyncPayload.class, ConfigSyncPayload.CODEC, ConfigAPI::handle).build();

    public ConfigAPI() {}

    private static void handle(ConfigSyncPayload payload, CustomPayloadEvent.Context ctx) {
        if (ctx.isServerSide()) {
            ConfigNetworking.onReceive(payload, ctx.getSender());
        } else {
            ClientConfigSync.onReceive(payload);
        }
    }
}
