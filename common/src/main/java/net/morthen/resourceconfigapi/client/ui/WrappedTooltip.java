package net.morthen.resourceconfigapi.client.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/**
 * Tooltip paragraph that wraps its text at a fixed width. Unlike one component per line,
 * vanilla only adds the gap below the first tooltip line once for the whole paragraph.
 */
public class WrappedTooltip implements ClientTooltipComponent {
    public static final int MAX_WIDTH = 170;

    private final Component text;

    public WrappedTooltip(Component text) {
        this.text = text;
    }

    @Override
    public int getHeight(Font font) {
        return font.lineHeight * font.split(this.text, MAX_WIDTH).size();
    }

    @Override
    public int getWidth(Font font) {
        return Math.min(font.width(this.text), MAX_WIDTH);
    }

    @Override
    public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
        List<FormattedCharSequence> lines = font.split(this.text, MAX_WIDTH);
        for (int i = 0; i < lines.size(); i++) {
            graphics.text(font, lines.get(i), x, y + font.lineHeight * i, -1, false);
        }
    }
}
