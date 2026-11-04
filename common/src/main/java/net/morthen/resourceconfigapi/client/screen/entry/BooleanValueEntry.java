package net.morthen.resourceconfigapi.client.screen.entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.morthen.resourceconfigapi.api.ConfigOption;
import net.morthen.resourceconfigapi.client.ui.widget.ThemedToggle;

public class BooleanValueEntry extends ValueListEntry<Boolean> {
    private final ThemedToggle toggle;

    public BooleanValueEntry(ConfigOption<?> option) {
        super(option);
        this.toggle = new ThemedToggle(this.option.get());
        this.addChild(this.toggle);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        if (!this.isEditable()) return false;

        // clicking anywhere on the card flips the value
        this.toggle.playDownSound(Minecraft.getInstance().getSoundManager());
        this.toggle.onPress();
        return true;
    }

    @Override
    protected AbstractWidget getControl() {
        return this.toggle;
    }

    @Override
    protected Boolean getWidgetValue() {
        return this.toggle.getValue();
    }

    @Override
    protected void setWidgetValue(Boolean value) {
        this.toggle.setValue(value);
    }
}
