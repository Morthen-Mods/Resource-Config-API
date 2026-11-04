package net.morthen.resourceconfigapi;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.morthen.resourceconfigapi.client.ClientConfigSync;
import net.morthen.resourceconfigapi.network.ConfigNetworking;
import net.morthen.resourceconfigapi.network.ConfigSyncPayload;

import java.util.function.Supplier;

@Mod(ConfigConstants.MOD_ID)
public class ConfigAPI {
    private static final String PROTOCOL = "1";

    // optional, so clients and servers without the mod can still connect
    public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder.named(ConfigConstants.of("main"))
            .networkProtocolVersion(() -> PROTOCOL)
            .clientAcceptedVersions(NetworkRegistry.acceptMissingOr(PROTOCOL))
            .serverAcceptedVersions(NetworkRegistry.acceptMissingOr(PROTOCOL))
            .simpleChannel();

    static {
        NETWORK.messageBuilder(ConfigSyncPayload.class, 0)
                .encoder(ConfigSyncPayload::encode)
                .decoder(ConfigSyncPayload::decode)
                .consumerMainThread(ConfigAPI::handle)
                .add();
    }

    public ConfigAPI() {}

    private static void handle(ConfigSyncPayload payload, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        if (ctx.getDirection().getReceptionSide().isServer()) {
            ConfigNetworking.onReceive(payload, ctx.getSender());
        } else {
            ClientConfigSync.onReceive(payload);
        }
    }
}
