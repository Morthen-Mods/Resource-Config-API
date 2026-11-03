package net.morthen.resourceconfigapi.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.morthen.resourceconfigapi.ConfigConstants;

/**
 * Values of one COMMON config, used in both directions:
 * server to client with every value, client to server with the values an op changed.
 */
public record ConfigSyncPayload(Identifier id, JsonObject values) implements CustomPacketPayload {
    public static final Type<ConfigSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ConfigConstants.MOD_ID, "sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPayload> CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, ConfigSyncPayload::id,
            ByteBufCodecs.lenientJson(Short.MAX_VALUE).map(JsonElement::getAsJsonObject, json -> json), ConfigSyncPayload::values,
            ConfigSyncPayload::new);

    @Override
    public Type<ConfigSyncPayload> type() {
        return TYPE;
    }
}
