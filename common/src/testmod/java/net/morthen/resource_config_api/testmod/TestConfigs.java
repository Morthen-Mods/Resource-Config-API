package net.morthen.resource_config_api.testmod;

import net.morthen.resource_config_api.api.*;

public class TestConfigs {

    public enum TestEnum {
        ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN;
    }

    @Config(fileName = "ClientConfigWithAnExtraLongNameForTesting", type = ConfigType.CLIENT)
    public static class ClientConfig {
        @ConfigEntry
        public static boolean testBoolean = false;

        @ConfigEntry
        public static int testInt = 100;

        @ConfigEntry(impact = EntryImpact.WORLD_RESTART)
        @RangedEntry(minValue = 25, maxValue = 50)
        public static int rangedInt = 45;

        @ConfigEntry(impact = EntryImpact.GAME_RESTART)
        public static String testString = "This is a test string";

        @ConfigEntry
        public static TestEnum testEnum = TestEnum.FIVE;
    }

    @Config(fileName = "CommonConfig", type = ConfigType.COMMON)
    public static class CommonConfig {
        @ConfigEntry
        public static boolean testBoolean = false;

        @ConfigEntry
        public static int testInt = 100;

        @ConfigEntry
        @RangedEntry(minValue = 25, maxValue = 50)
        public static int rangedInt = 45;

        @ConfigEntry
        public static String testString = "This is a test string";

        @ConfigEntry
        public static TestEnum testEnum = TestEnum.FIVE;
    }
}
