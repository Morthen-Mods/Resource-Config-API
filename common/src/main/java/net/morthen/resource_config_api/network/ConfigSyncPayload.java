package net.morthen.resource_config_api.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.morthen.resource_config_api.ConfigConstants;

/**
 * Config values of one COMMON config, used in both directions:
 * server → client with every value, client → server with the entries an op changed.
 */
public record ConfigSyncPayload(Identifier id, JsonObject values) implements CustomPacketPayload {
    public static final Type<ConfigSyncPayload> TYPE = new Type<>(ConfigConstants.of("sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPayload> CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, ConfigSyncPayload::id,
            ByteBufCodecs.lenientJson(Short.MAX_VALUE).map(JsonElement::getAsJsonObject, json -> json), ConfigSyncPayload::values,
            ConfigSyncPayload::new);

    @Override
    public Type<ConfigSyncPayload> type() {
        return TYPE;
    }
}
