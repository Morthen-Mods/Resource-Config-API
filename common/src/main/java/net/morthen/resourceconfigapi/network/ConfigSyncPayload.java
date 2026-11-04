package net.morthen.resourceconfigapi.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.morthen.resourceconfigapi.ConfigConstants;

/**
 * Values of one COMMON config, used in both directions:
 * server to client with every value, client to server with the values an op changed.
 */
public record ConfigSyncPayload(ResourceLocation id, JsonObject values) implements CustomPacketPayload {
    public static final Type<ConfigSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ConfigConstants.MOD_ID, "sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPayload> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ConfigSyncPayload::id,
            ByteBufCodecs.stringUtf8(Short.MAX_VALUE).map(text -> JsonParser.parseString(text).getAsJsonObject(), JsonObject::toString), ConfigSyncPayload::values,
            ConfigSyncPayload::new);

    @Override
    public Type<ConfigSyncPayload> type() {
        return TYPE;
    }
}
