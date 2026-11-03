package net.morthen.resourceconfigapi.client.ui.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.morthen.resourceconfigapi.client.ui.Themes;

/** Drawing code shared by {@link ThemedObjectList} and {@link ThemedContainerList}. */
final class ThemedLists {
    private ThemedLists() {}

    static void extractSelection(GuiGraphicsExtractor graphics, int x, int y, int width, int height, boolean focused) {
        int left = x;
        int top = y + 1;
        int right = x + width;
        int bottom = y + height - 1;

        Themes.get().fillRounded(graphics, left, top, right, bottom, Themes.get().selected);
        graphics.fill(left, top + 2, left + 2, bottom - 2, Themes.get().accent);
        if (focused) Themes.get().outlineRounded(graphics, left, top, right, bottom, 0x80FFFFFF);
    }

    static void extractEmptyMessage(GuiGraphicsExtractor graphics, AbstractWidget list, Component message) {
        if (message == null) return;
        graphics.centeredText(Minecraft.getInstance().font, message, list.getX() + list.getWidth() / 2, list.getY() + 12, Themes.get().textMuted);
    }
}
