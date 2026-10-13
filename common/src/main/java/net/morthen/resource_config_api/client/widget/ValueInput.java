package net.morthen.resource_config_api.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.morthen.resource_config_api.api.ModConfig;
import net.morthen.resource_config_api.api.RangedEntry;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record ValueInput(AbstractWidget widget, Supplier<Object> read, Consumer<Object> write) {
    private static final Set<Class<?>> INTEGRAL_TYPES = Set.of(
            byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class, Long.class);

    public static ValueInput create(ModConfig config, String key) {
        Class<?> type = config.valueType(key);
        Font font = Minecraft.getInstance().font;

        if (type == boolean.class || type == Boolean.class) {
            Checkbox checkbox = Checkbox.builder(Component.empty(), font).build();
            return new ValueInput(checkbox, checkbox::selected, value -> checkbox.selected = (Boolean) value);
        }

        if (type.isEnum()) {
            String translationKey = config.translationKey(key);
            DropdownWidget<Object> dropdown = new DropdownWidget<>(Arrays.asList(type.getEnumConstants()), value ->
                    Component.translatableWithFallback(translationKey + "." + ((Enum<?>) value).name(), value.toString()));
            return new ValueInput(dropdown, dropdown::getSelected, dropdown::setSelected);
        }

        RangedEntry range = config.range(key);
        if (range != null) {
            RangedSlider slider = new RangedSlider(range, INTEGRAL_TYPES.contains(type));
            return new ValueInput(slider, slider::getActualValue, value -> slider.setActualValue(((Number) value).doubleValue()));
        }

        EditBox editBox = new EditBox(font, type == String.class ? 150 : 100, 20, Component.empty());
        editBox.setMaxLength(1024);
        return new ValueInput(editBox, editBox::getValue, value -> editBox.setValue(String.valueOf(value)));
    }
}
