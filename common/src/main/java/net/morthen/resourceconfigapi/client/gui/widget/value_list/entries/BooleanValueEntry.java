package net.morthen.resourceconfigapi.client.gui.widget.value_list.entries;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.morthen.resourceconfigapi.client.ClientConstants;
import net.morthen.resourceconfigapi.client.gui.screen.ResourceConfigScreen;
import net.morthen.resourceconfigapi.client.gui.widget.value_list.base.ButtonValueEntry;
import net.morthen.resourceconfigapi.client.util.GuiUtils;

import java.lang.reflect.Field;
import java.util.Objects;

public class BooleanValueEntry extends ButtonValueEntry<Boolean> {

    private boolean state;

    public BooleanValueEntry(ResourceConfigScreen screen, String modId, String fileName, String translationKey, Field field, Object defaultValue) {
        super(screen, modId, fileName, translationKey, field, defaultValue);

        this.state = this.getFieldValue();

        this.valueButton = Button.builder(state ? ClientConstants.BOOLEAN_ENABLED : ClientConstants.BOOLEAN_DISABLED, this::changeState)
                .tooltip(GuiUtils.hasTranslation(tooltip) ? Tooltip.create(tooltip) : null)
                .bounds(0, 0, getWidgetWidth(), 20)
                .build();

        this.children.add(valueButton);
    }


    private void changeState(Button button) {
        state = !state;

        button.setMessage(state ? ClientConstants.BOOLEAN_ENABLED : ClientConstants.BOOLEAN_DISABLED);

        changeUndoState(!Objects.equals(state, getFieldValue()));
    }

    @Override
    public Boolean getValue() {
        return state;
    }

    @Override
    public void undoChanges() {
        state = getFieldValue();
        valueButton.setMessage(state ? ClientConstants.BOOLEAN_ENABLED : ClientConstants.BOOLEAN_DISABLED);
        changeUndoState(false);
    }

    @Override
    public void resetValues() {
        state = (boolean) this.defaultValue;
        valueButton.setMessage(state ? ClientConstants.BOOLEAN_ENABLED : ClientConstants.BOOLEAN_DISABLED);
        changeUndoState(!Objects.equals(state, getFieldValue()));
        super.resetValues();
    }
}
