package net.morthen.resourceconfigapi.client.ui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.morthen.resourceconfigapi.client.ui.Themes;

/** Square button that only shows a sprite and a subtle background while hovered. */
public class ThemedIconButton extends AbstractButton {
    private final Identifier sprite;
    private final Identifier hoveredSprite;
    private final int iconSize;
    private final OnPress onPress;

    /**
     * @param size width and height of the button
     * @param sprite gui sprite of the icon
     * @param hoveredSprite sprite used while hovered, may be the same as {@code sprite}
     * @param iconSize width and height of the icon, it gets centered inside the button
     * @param narration text read by the narrator, also used as message
     */
    public ThemedIconButton(int size, Identifier sprite, Identifier hoveredSprite, int iconSize, Component narration, OnPress onPress) {
        super(0, 0, size, size, narration);
        this.sprite = sprite;
        this.hoveredSprite = hoveredSprite;
        this.iconSize = iconSize;
        this.onPress = onPress;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        this.onPress.onPress(this);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        boolean highlighted = this.active && this.isHoveredOrFocused();
        if (highlighted) {
            Themes.get().fillRounded(graphics, this.getX(), this.getY(), this.getRight(), this.getBottom(), Themes.get().cardHovered);
        }
        if (this.isFocused()) {
            Themes.get().outlineRounded(graphics, this.getX(), this.getY(), this.getRight(), this.getBottom(), Themes.get().controlBorderFocused);
        }

        int offset = (this.width - this.iconSize) / 2;
        int color = this.active ? ARGB.white(this.alpha) : ARGB.color(this.alpha, 0x707070);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, highlighted ? this.hoveredSprite : this.sprite,
                this.getX() + offset, this.getY() + offset, this.iconSize, this.iconSize, color);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    public interface OnPress {
        void onPress(ThemedIconButton button);
    }
}
