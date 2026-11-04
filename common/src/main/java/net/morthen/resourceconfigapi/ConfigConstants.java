package net.morthen.resourceconfigapi;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigConstants {
	public static final String MOD_ID = "resourceconfigapi";
	public static final String MOD_NAME = "Resource Config API";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	public static ResourceLocation of(String id) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
	}
}
