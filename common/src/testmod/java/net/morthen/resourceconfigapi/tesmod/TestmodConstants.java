package net.morthen.resourceconfigapi.tesmod;

import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.api.ConfigRegistry;
import net.morthen.resourceconfigapi.api.ConfigType;
import net.morthen.resourceconfigapi.api.annotation.ColorEntry;
import net.morthen.resourceconfigapi.api.annotation.Config;
import net.morthen.resourceconfigapi.api.annotation.ConfigEntry;
import net.morthen.resourceconfigapi.api.annotation.RangedEntry;

import java.util.Arrays;
import java.util.List;

public class TestmodConstants {
    public static final String MOD_ID = ConfigConstants.MOD_ID + "_testmod";
    public static final String MOD_NAME = ConfigConstants.MOD_NAME + " - Testmod";

    public static void commonInit() {
        ConfigRegistry.register(ConfigOne.class, MOD_ID);
        ConfigRegistry.register(ConfigTwo.class, MOD_ID);
        ConfigRegistry.register(ConfigThree.class, MOD_ID);
        ConfigRegistry.register(ConfigFour.class, MOD_ID);
    }

    public enum TestEnum {
        ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN;

        public static List<TestEnum> asList() {
            return Arrays.stream(values()).toList();
        }
    }

    @Config(fileName = "config_one", type = ConfigType.CLIENT)
    public static class ConfigOne {
        @ConfigEntry(category = "category_one")
        public static boolean testBoolean = true;

        @ConfigEntry(category = "category_one", needsGameRestart = true)
        public static int testInteger = 200;

        @ConfigEntry(category = "category_one", needsWorldRestart = true)
        public static double testDouble = 20.2423;

        @ConfigEntry
        public static short testShort = 32000;

        @ConfigEntry
        @RangedEntry(minValue = 10, maxValue = 25)
        public static float testFloat = 15f;

        @ConfigEntry
        public static long testLong = 32000L;

        @ConfigEntry
        public static String testString = "This is a Test";

        @ConfigEntry
        public static List<String> testStringList = List.of("One",  "Two", "Three");

        @ConfigEntry
        public static List<Integer> testIntegerList = List.of(1, 2, 3);

        @ConfigEntry
        public static TestEnum testEnum = TestEnum.ONE;

        @ConfigEntry(category = "category_two")
        public static Integer testBoxedInteger = 5;

        @ConfigEntry(category = "category_two", translation = "shared_option")
        @RangedEntry(minValue = 0, maxValue = 10)
        public static int testSharedOne = 3;

        @ConfigEntry(category = "category_one", translation = "shared_option")
        public static Boolean testSharedTwo = false;

        @ConfigEntry(category = "colors")
        @ColorEntry
        public static int testColor = 0xFF8800;

        @ConfigEntry(category = "colors")
        @ColorEntry(alpha = true)
        public static int testColorAlpha = 0x8033AAFF;
    }

    @Config(fileName = "config_two", type = ConfigType.CLIENT)
    public static class ConfigTwo {
        @ConfigEntry
        public static int testInteger = 100;
    }

    @Config(fileName = "config_three", type = ConfigType.COMMON)
    public static class ConfigThree {
        @ConfigEntry
        public static int testInteger = 100;
    }

    @Config(fileName = "config_four", type = ConfigType.COMMON)
    public static class ConfigFour {
        @ConfigEntry
        public static int testInteger = 100;
    }
}
