package net.morthen.resourceconfigapi.client.screen.entry;

import net.morthen.resourceconfigapi.api.ColorMode;
import net.morthen.resourceconfigapi.api.ConfigFile;
import net.morthen.resourceconfigapi.api.ConfigOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleFunction;
import java.util.function.Function;

/** Creates the value list rows of a config, a new set for every screen. */
public final class ValueEntryFactory {
    private record NumberType(boolean decimal, Function<String, Number> parser, DoubleFunction<Number> converter) {}

    private static final NumberType INT = new NumberType(false, Integer::parseInt, value -> (int) Math.round(value));
    private static final NumberType LONG = new NumberType(false, Long::parseLong, Math::round);
    private static final NumberType SHORT = new NumberType(false, Short::parseShort, value -> (short) Math.round(value));
    private static final NumberType BYTE = new NumberType(false, Byte::parseByte, value -> (byte) Math.round(value));
    private static final NumberType DOUBLE = new NumberType(true, Double::parseDouble, value -> value);
    private static final NumberType FLOAT = new NumberType(true, Float::parseFloat, value -> (float) value);

    private static final Map<Class<?>, NumberType> NUMBER_TYPES = Map.ofEntries(
            Map.entry(int.class, INT), Map.entry(Integer.class, INT),
            Map.entry(long.class, LONG), Map.entry(Long.class, LONG),
            Map.entry(short.class, SHORT), Map.entry(Short.class, SHORT),
            Map.entry(byte.class, BYTE), Map.entry(Byte.class, BYTE),
            Map.entry(double.class, DOUBLE), Map.entry(Double.class, DOUBLE),
            Map.entry(float.class, FLOAT), Map.entry(Float.class, FLOAT)
    );

    private ValueEntryFactory() {}

    /**
     * Options without category come first and get no header,
     * the categories follow in the order of their first option.
     */
    public static List<ValueListRow> create(ConfigFile config) {
        List<ValueListRow> rows = new ArrayList<>();

        for (ConfigOption<?> option : config.options()) {
            if (option.category().isEmpty()) rows.add(createEntry(option));
        }

        for (String category : config.categories()) {
            rows.add(new CategoryEntry(config, category));
            for (ConfigOption<?> option : config.options()) {
                if (option.category().equals(category)) rows.add(createEntry(option));
            }
        }

        return rows;
    }

    public static ValueListEntry<?> createEntry(ConfigOption<?> option) {
        Class<?> type = option.type();

        if (type == boolean.class || type == Boolean.class) return new BooleanValueEntry(option);
        if (type == String.class) return new TextValueEntry<>(option, text -> text, 150, false);
        if (type.isEnum()) return new EnumValueEntry(option);
        if (option.colorMode() != ColorMode.NONE) return new ColorValueEntry(option);

        NumberType number = NUMBER_TYPES.get(type);
        if (number != null) {
            return option.range()
                    .<ValueListEntry<?>>map(range -> new SliderValueEntry<>(option, range, number.decimal(), number.converter()))
                    .orElseGet(() -> new TextValueEntry<>(option, number.parser(), 100, true));
        }

        return new ValueListEntry.Unsupported(option);
    }
}
