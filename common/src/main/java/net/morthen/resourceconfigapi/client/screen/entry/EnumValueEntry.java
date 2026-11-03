package net.morthen.resourceconfigapi.client.screen.entry;

import net.minecraft.client.gui.components.AbstractWidget;
import net.morthen.resourceconfigapi.api.ConfigOption;
import net.morthen.resourceconfigapi.client.ui.widget.PopupWidget;
import net.morthen.resourceconfigapi.client.ui.widget.ThemedDropdown;

import java.util.Arrays;

public class EnumValueEntry extends ValueListEntry<Object> {
    private final ThemedDropdown<Object> dropdown;

    public EnumValueEntry(ConfigOption<?> option) {
        super(option);
        this.dropdown = new ThemedDropdown<>(Arrays.asList(option.type().getEnumConstants()), ((Enum<?>) option.get()).ordinal());
        this.addChild(this.dropdown);
    }

    @Override
    public PopupWidget getPopup() {
        return this.dropdown;
    }

    @Override
    protected AbstractWidget getControl() {
        return this.dropdown;
    }

    @Override
    protected Object getWidgetValue() {
        return this.dropdown.getSelected();
    }

    @Override
    protected void setWidgetValue(Object value) {
        this.dropdown.setSelected(value);
    }
}
