package net.morthen.resourceconfigapi.client.ui.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.morthen.resourceconfigapi.client.ui.Themes;
import net.morthen.resourceconfigapi.client.ui.UiUtil;

/** Drawing code shared by {@link ThemedObjectList} and {@link ThemedContainerList}. */
final class ThemedLists {
    /** Width of the scrollbar hit area, the vanilla lists use the same. */
    static final int SCROLLBAR_WIDTH = 6;

    private ThemedLists() {}

    static void renderSelection(GuiGraphics graphics, int x, int y, int width, int height, boolean focused) {
        int left = x;
        int top = y + 1;
        int right = x + width;
        int bottom = y + height - 1;

        Themes.get().fillRounded(graphics, left, top, right, bottom, Themes.get().selected);
        graphics.fill(left, top + 2, left + 2, bottom - 2, Themes.get().accent);
        if (focused) Themes.get().outlineRounded(graphics, left, top, right, bottom, 0x80FFFFFF);
    }

    static void renderEmptyMessage(GuiGraphics graphics, AbstractWidget list, Component message) {
        if (message == null) return;
        graphics.drawCenteredString(Minecraft.getInstance().font, message, list.getX() + list.getWidth() / 2, list.getY() + 12, Themes.get().textMuted);
    }

    /**
     * Draws the slim scrollbar with the same thumb size and position the vanilla list uses
     * for dragging, so the thumb stays under the mouse.
     */
    static void renderScrollbar(GuiGraphics graphics, AbstractWidget list, int barX, double scrollAmount, int maxScroll,
                                int maxPosition, int mouseX, int mouseY) {
        int height = list.getHeight();
        int thumbHeight = Mth.clamp((int) ((float) (height * height) / maxPosition), 32, height - 8);
        int thumbY = Math.max(list.getY(), (int) scrollAmount * (height - thumbHeight) / maxScroll + list.getY());
        boolean hovered = UiUtil.inBound(barX, list.getY(), barX + SCROLLBAR_WIDTH, list.getBottom(), mouseX, mouseY);

        Themes.get().scrollbar(graphics, barX, list.getY(), SCROLLBAR_WIDTH, height, thumbY, thumbHeight, hovered);
    }
}
