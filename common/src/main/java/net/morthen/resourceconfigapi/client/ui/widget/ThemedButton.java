package net.morthen.resourceconfigapi.client.ui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.morthen.resourceconfigapi.client.ui.Theme;
import net.morthen.resourceconfigapi.client.ui.Themes;

/**
 * Flat button with a slight bevel. Comes in three styles, {@link Style#PRIMARY}
 * for the main action of a screen and {@link Style#DANGER} for destructive ones.
 * <pre>
 * ThemedButton.builder(Component.literal("Save"), button -> save())
 *         .style(ThemedButton.Style.PRIMARY).width(100).build();
 * </pre>
 */
public class ThemedButton extends AbstractButton {
    /** The kinds of buttons, every {@link Theme} provides colors for each of them. */
    public enum Style { DEFAULT, PRIMARY, DANGER }

    private final OnPress onPress;
    private Style style;

    protected ThemedButton(int x, int y, int width, int height, Component message, OnPress onPress, Style style) {
        super(x, y, width, height, message);
        this.onPress = onPress;
        this.style = style;
    }

    public static Builder builder(Component message, OnPress onPress) {
        return new Builder(message, onPress);
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    /** Sets size and position at once, 1.20.1 has no {@code setRectangle} yet. */
    public void setRectangle(int width, int height, int x, int y) {
        this.width = width;
        this.height = height;
        this.setPosition(x, y);
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int left = this.getX(), top = this.getY(), right = (this.getX() + this.width), bottom = (this.getY() + this.height);
        boolean highlighted = this.active && this.isHoveredOrFocused();

        Themes.get().button(graphics, left, top, right, bottom, this.style, highlighted, this.active);

        int color = this.active ? Themes.get().text : Themes.get().textDisabled;
        this.renderScrollingString(graphics, Minecraft.getInstance().font, 4, color);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    public interface OnPress {
        void onPress(ThemedButton button);
    }

    public static class Builder {
        private final Component message;
        private final OnPress onPress;
        private Style style = Style.DEFAULT;
        private Tooltip tooltip;
        private int x, y;
        private int width = 150, height = 20;

        private Builder(Component message, OnPress onPress) {
            this.message = message;
            this.onPress = onPress;
        }

        public Builder style(Style style) {
            this.style = style;
            return this;
        }

        public Builder tooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder bounds(int x, int y, int width, int height) {
            return this.pos(x, y).size(width, height);
        }

        public ThemedButton build() {
            ThemedButton button = new ThemedButton(this.x, this.y, this.width, this.height, this.message, this.onPress, this.style);
            button.setTooltip(this.tooltip);
            return button;
        }
    }
}
