package net.morthen.resource_config_api.client.list;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CategoryRow extends Row {
    private final Component label;

    public CategoryRow(Component label) {
        this.label = label.copy().withStyle(ChatFormatting.GOLD);
    }

    @Override
    public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        graphics.centeredText(font, label, getContentXMiddle(), getContentYMiddle() - font.lineHeight / 2, -1);
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of();
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of();
    }
}
