package net.morthen.resourceconfigapi.client.gui.widget.value_list.base;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.client.gui.screen.ResourceConfigScreen;
import net.morthen.resourceconfigapi.client.gui.tooltip.ResourceTooltipProvider;
import net.morthen.resourceconfigapi.client.util.ComponentUtils;
import net.morthen.resourceconfigapi.client.util.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseEntry extends ContainerObjectSelectionList.Entry<BaseEntry> implements ResourceTooltipProvider {

    public final Identifier undoSprite = ConfigConstants.of("textures/gui/sprites/icon/undo.png");
    private final int widgetWidth = 160;

    protected final List<AbstractWidget> children = new ArrayList<>();
    protected final Font font = GuiUtils.getFont();

    protected final Component label, tooltip;
    protected final ResourceConfigScreen screen;

    /**
     *
     * @param fileName is the fileName of the given config
     * @param key is the Category or Value name
     * @param chatFormatting is formatting for the label, tooltips cant be changed currently
     */
    public BaseEntry(ResourceConfigScreen screen, String modId, String fileName, String key, ChatFormatting chatFormatting) {
        this.label = ComponentUtils.modLabel(modId, fileName, key).copy().withStyle(chatFormatting);
        this.tooltip = ComponentUtils.modTooltip(modId, fileName, key);
        this.screen = screen;
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return this.children;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.children;
    }

    @Override
    public abstract void extractContent(GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, boolean b, float v);

    public Font getFont() {
        return font;
    }

    public int getWidgetWidth() {
        return widgetWidth;
    }

    public void saveValues() {}

    public void undoChanges() {}

    public void resetValues() {}

    @Override
    public Component getTooltip() {
        return this.tooltip;
    }
}
