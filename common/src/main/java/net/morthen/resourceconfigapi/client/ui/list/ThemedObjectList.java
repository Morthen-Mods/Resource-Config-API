package net.morthen.resourceconfigapi.client.ui.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.morthen.resourceconfigapi.client.ui.Themes;

/**
 * Selection list without the vanilla background and separators, with a slim scrollbar and
 * the selected entry marked by an accent bar. Meant to be placed on top of a panel.
 */
public abstract class ThemedObjectList<E extends ObjectSelectionList.Entry<E>> extends ObjectSelectionList<E> {
    private Component emptyMessage;

    public ThemedObjectList(int itemHeight) {
        super(Minecraft.getInstance(), 0, 0, 0, itemHeight);
    }

    /** Text shown in the list while it has no entries. */
    public void setEmptyMessage(Component emptyMessage) {
        this.emptyMessage = emptyMessage;
    }

    public E getHoveredEntry() {
        return this.getHovered();
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
        ThemedLists.extractEmptyMessage(graphics, this, this.children().isEmpty() ? this.emptyMessage : null);
    }

    @Override
    protected void extractSelection(GuiGraphicsExtractor graphics, E entry, int outlineColor) {
        ThemedLists.extractSelection(graphics, entry.getX(), entry.getY(), entry.getWidth(), entry.getHeight(), this.isFocused());
    }

    @Override
    protected void extractListBackground(GuiGraphicsExtractor graphics) {}

    @Override
    protected void extractListSeparators(GuiGraphicsExtractor graphics) {}

    @Override
    protected void extractScrollbar(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (!this.scrollable()) return;
        Themes.get().scrollbar(graphics, this.scrollBarX(), this.getY(), this.scrollbarWidth(), this.getHeight(),
                this.scrollBarY(), this.scrollerHeight(), this.isOverScrollbar(mouseX, mouseY));
    }

    @Override
    public int getRowLeft() {
        return this.getX() + 2;
    }

    @Override
    public int getRowWidth() {
        return this.width - 4 - (this.scrollable() ? this.scrollbarWidth() + 2 : 0);
    }

    @Override
    protected int scrollBarX() {
        return this.getRight() - this.scrollbarWidth() - 1;
    }
}
