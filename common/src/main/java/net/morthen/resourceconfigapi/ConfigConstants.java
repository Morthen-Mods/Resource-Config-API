package net.morthen.resourceconfigapi;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigConstants {

	public static final String MOD_ID = "resourceconfigapi";
	public static final String MOD_NAME = "Resource Config API";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	public static Identifier of(String id) {
		return Identifier.fromNamespaceAndPath(MOD_ID, id);
	}
}