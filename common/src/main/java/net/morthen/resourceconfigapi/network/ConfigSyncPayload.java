package net.morthen.resourceconfigapi.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.morthen.resourceconfigapi.ConfigConstants;

/**
 * Values of one COMMON config, used in both directions:
 * server to client with every value, client to server with the values an op changed.
 * <p>
 * 1.20.1 has no payload types yet, the loaders send it on {@link #CHANNEL} with {@link #encode} and {@link #decode}.
 */
public record ConfigSyncPayload(ResourceLocation id, JsonObject values) {
    public static final ResourceLocation CHANNEL = ConfigConstants.of("sync");
    private static final int MAX_LENGTH = Short.MAX_VALUE;

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.id);
        buf.writeUtf(this.values.toString(), MAX_LENGTH);
    }

    public static ConfigSyncPayload decode(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        JsonObject values = JsonParser.parseString(buf.readUtf(MAX_LENGTH)).getAsJsonObject();
        return new ConfigSyncPayload(id, values);
    }
}
