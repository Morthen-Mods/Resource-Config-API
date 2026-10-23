package net.morthen.resourceconfigapi;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.morthen.resourceconfigapi.api.ConfigRegistry;
import net.morthen.resourceconfigapi.config.ModConfig;
import net.morthen.resourceconfigapi.network.ConfigNetwork;
import net.morthen.resourceconfigapi.network.payloads.ConfigSyncPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ConfigConstants {

	public static final String MOD_ID = "resourceconfigapi";
	public static final String MOD_NAME = "Resource Config API";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static Identifier of(String id) {
		return Identifier.fromNamespaceAndPath(MOD_ID, id);
	}
}