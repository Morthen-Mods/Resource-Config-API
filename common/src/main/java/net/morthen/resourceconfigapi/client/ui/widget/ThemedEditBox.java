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
        super(font, 0, 0, width, height, narration);
        // 1.20.1 only allows to turn the vanilla border off as a whole, the padding is added in renderWidget and onClick
        this.setBordered(false);
    }

    /** Sets size and position at once, 1.20.1 has no {@code setRectangle} yet. */
    public void setRectangle(int width, int height, int x, int y) {
        this.width = width;
        this.height = height;
        this.setPosition(x, y);
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

    // the box is drawn without the vanilla border, but with the same padding as a bordered one
    private static final int PADDING = 4;

    @Override
    public int getInnerWidth() {
        return this.width - 2 * PADDING;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX - PADDING, mouseY);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.isVisible()) {
            Themes.get().control(graphics, this.getX(), this.getY(), (this.getX() + this.width), (this.getY() + this.height),
                    this.isHovered(), this.isFocused(), this.isActive(), this.error ? Themes.get().invalid : 0);
        }

        // vanilla only grays out the text of non editable boxes, disabled ones should look the same
        super.setTextColor(!this.isActive() ? Themes.get().textDisabled : this.error ? Themes.get().invalid : this.textColor);
        // the unbordered vanilla box draws the text at the top left corner, so it gets moved into the padding
        graphics.pose().pushPose();
        graphics.pose().translate(PADDING, (this.height - 8) / 2f, 0);
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        graphics.pose().popPose();
    }
}
