package net.morthen.resourceconfigapi.client.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

/** Theme independent layout and text helpers shared by the widgets. */
public final class UiUtil {
    private static final String ELLIPSIS = "...";

    private UiUtil() {}

    /** Returns the y position that vertically centers an element of the given height between top and bottom. */
    public static int centerY(int top, int bottom, int height) {
        return top + (bottom - top - height) / 2;
    }

    /** Cuts the text to the given width and appends "..." if it doesn't fit. */
    public static FormattedCharSequence ellipsize(Font font, Component text, int maxWidth) {
        if (font.width(text) <= maxWidth) return text.getVisualOrderText();

        FormattedText cut = font.substrByWidth(text, Math.max(0, maxWidth - font.width(ELLIPSIS)));
        return Language.getInstance().getVisualOrder(FormattedText.composite(cut, FormattedText.of(ELLIPSIS)));
    }

    public static boolean inBound(int left, int top, int right, int bottom, double mouseX, double mouseY) {
        return mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom;
    }
}
