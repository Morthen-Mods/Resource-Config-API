package net.morthen.resourceconfigapi.client.ui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.morthen.resourceconfigapi.client.ui.Themes;

import java.util.function.Predicate;

/**
 * Vanilla edit box with the control background of the theme instead of the sprite.
 * {@link #setError(boolean)} marks the input as invalid with a red border and text,
 * {@link #setFilter(Predicate)} rejects input that doesn't match.
 */
public class ThemedEditBox extends EditBox {
    private boolean error;
    private int textColor = Themes.get().text;
    private Predicate<String> filter = s -> true;

    public ThemedEditBox(Font font, int width, int height, Component narration) {
        super(font, width, height, narration);
    }

    /** Typed or pasted text is rejected if the resulting value doesn't match the filter. */
    public void setFilter(Predicate<String> filter) {
        this.filter = filter;
    }

    @Override
    public void insertText(String input) {
        String old = this.getValue();
        int cursor = this.getCursorPosition();
        super.insertText(input);

        if (!this.filter.test(this.getValue())) {
            this.setValue(old);
            this.setCursorPosition(cursor);
        }
    }

    public void setError(boolean error) {
        this.error = error;
        super.setTextColor(error ? Themes.get().invalid : this.textColor);
    }

    public boolean hasError() {
        return this.error;
    }

    @Override
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        if (!this.error) super.setTextColor(textColor);
    }

    // The vanilla sprite is only drawn while isBordered() returns true, the text position still uses
    // the bordered padding, because it reads the field directly. So the inner width has to match it.
    @Override
    public boolean isBordered() {
        return false;
    }

    @Override
    public int getInnerWidth() {
        return this.width - 8;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.isVisible()) {
            Themes.get().control(graphics, this.getX(), this.getY(), this.getRight(), this.getBottom(),
                    this.isHovered(), this.isFocused(), this.isActive(), this.error ? Themes.get().invalid : 0);
        }

        // vanilla only grays out the text of non editable boxes, disabled ones should look the same
        super.setTextColor(!this.isActive() ? Themes.get().textDisabled : this.error ? Themes.get().invalid : this.textColor);
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
    }
}
