package net.morthen.resourceconfigapi.client.screen.entry;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.morthen.resourceconfigapi.api.ConfigFile;
import net.morthen.resourceconfigapi.client.ui.Themes;
import net.morthen.resourceconfigapi.client.ui.UiUtil;

/** Header shown above the options of a category. */
public class CategoryEntry extends ValueListRow {
    public static final int HEIGHT = 22;

    private final Component label;

    public CategoryEntry(ConfigFile config, String category) {
        this.label = Component.translatable(config.categoryLabelKey(category));
    }

    @Override
    public int getRowHeight() {
        return HEIGHT;
    }

    @Override
    public String getSearchText() {
        return this.label.getString();
    }

    @Override
    public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        int x = this.getX() + 2;
        int textY = this.getY() + this.getHeight() - font.lineHeight - 3;

        graphics.text(font, UiUtil.ellipsize(font, label, this.getWidth() - 4), x, textY, Themes.get().accent);

        // divider that runs from the end of the label to the right edge
        int lineX = x + Math.min(font.width(label), this.getWidth() - 4) + 6;
        int lineY = textY + font.lineHeight / 2 - 1;
        if (lineX < this.getX() + this.getWidth()) {
            graphics.fill(lineX, lineY, this.getX() + this.getWidth(), lineY + 1, Themes.get().divider);
        }
    }
}
