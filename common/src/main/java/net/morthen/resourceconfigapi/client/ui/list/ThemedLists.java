package net.morthen.resourceconfigapi.client.ui.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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

    static void renderEmptyMessage(GuiGraphics graphics, int left, int top, int width, Component message) {
        if (message == null) return;
        graphics.drawCenteredString(Minecraft.getInstance().font, message, left + width / 2, top + 12, Themes.get().textMuted);
    }

    /**
     * Draws the slim scrollbar with the same thumb size and position the vanilla list uses
     * for dragging, so the thumb stays under the mouse.
     */
    static void renderScrollbar(GuiGraphics graphics, int barX, int top, int bottom, double scrollAmount, int maxScroll,
                                int maxPosition, int mouseX, int mouseY) {
        int height = bottom - top;
        int thumbHeight = Mth.clamp((int) ((float) (height * height) / maxPosition), 32, height - 8);
        int thumbY = Math.max(top, (int) scrollAmount * (height - thumbHeight) / maxScroll + top);
        boolean hovered = UiUtil.inBound(barX, top, barX + SCROLLBAR_WIDTH, bottom, mouseX, mouseY);

        Themes.get().scrollbar(graphics, barX, top, SCROLLBAR_WIDTH, height, thumbY, thumbHeight, hovered);
    }
}
