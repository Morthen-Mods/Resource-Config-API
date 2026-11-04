package net.morthen.resourceconfigapi.client.ui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.morthen.resourceconfigapi.client.ui.Themes;
import net.morthen.resourceconfigapi.client.ui.UiUtil;

/** Square button that only shows an icon and a subtle background while hovered. */
public class ThemedIconButton extends AbstractButton {
    private final ResourceLocation texture;
    private final ResourceLocation hoveredTexture;
    private final int textureSize;
    private final int iconSize;
    private final OnPress onPress;

    /**
     * @param size width and height of the button
     * @param texture full path of the square icon texture, like {@code textures/gui/sprites/widget/undo.png}
     * @param hoveredTexture texture used while hovered, may be the same as {@code texture}
     * @param textureSize width and height of the texture files in pixels
     * @param iconSize width and height of the icon, it gets centered inside the button
     * @param narration text read by the narrator, also used as message
     */
    public ThemedIconButton(int size, ResourceLocation texture, ResourceLocation hoveredTexture, int textureSize, int iconSize, Component narration, OnPress onPress) {
        super(0, 0, size, size, narration);
        this.texture = texture;
        this.hoveredTexture = hoveredTexture;
        this.textureSize = textureSize;
        this.iconSize = iconSize;
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean highlighted = this.active && this.isHoveredOrFocused();
        if (highlighted) {
            Themes.get().fillRounded(graphics, this.getX(), this.getY(), (this.getX() + this.width), (this.getY() + this.height), Themes.get().cardHovered);
        }
        if (this.isFocused()) {
            Themes.get().outlineRounded(graphics, this.getX(), this.getY(), (this.getX() + this.width), (this.getY() + this.height), Themes.get().controlBorderFocused);
        }

        int offset = (this.width - this.iconSize) / 2;
        float shade = this.active ? 1 : 0x70 / 255f;
        graphics.setColor(shade, shade, shade, this.alpha);
        UiUtil.blitTexture(graphics, highlighted ? this.hoveredTexture : this.texture,
                this.getX() + offset, this.getY() + offset, this.iconSize, this.iconSize, this.textureSize);
        graphics.setColor(1, 1, 1, 1);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    public interface OnPress {
        void onPress(ThemedIconButton button);
    }
}
