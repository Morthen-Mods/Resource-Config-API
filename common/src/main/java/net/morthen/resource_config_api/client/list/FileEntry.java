package net.morthen.resource_config_api.client.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.morthen.resource_config_api.api.ModConfig;

public class FileEntry extends ObjectSelectionList.Entry<FileEntry> {
    private final Font font = Minecraft.getInstance().font;
    private final ModConfig config;
    private final Component label;
    private final Component tooltip;

    public FileEntry(ModConfig config) {
        this.config = config;
        this.label = Component.translatableWithFallback(config.translationKey() + ".label", config.fileName());
        this.tooltip = Component.translatable(config.translationKey() + ".tooltip");
    }

    public ModConfig getConfig() {
        return config;
    }

    public int getLabelWidth() {
        return font.width(label);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return true; // the list only selects entries that handle the click
    }

    @Override
    public Component getNarration() {
        return label;
    }

    @Override
    public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        graphics.text(font, label, getContentX() + 2, getContentYMiddle() - font.lineHeight / 2, -1, false);
        if (hovered && ComponentUtils.isTranslationResolvable(tooltip)) {
            graphics.setTooltipForNextFrame(font, font.split(tooltip, 200), mouseX, mouseY);
        }
    }
}
