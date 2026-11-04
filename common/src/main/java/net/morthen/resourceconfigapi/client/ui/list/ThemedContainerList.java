package net.morthen.resourceconfigapi.client.ui.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.network.chat.Component;

/**
 * List whose entries contain widgets, without the vanilla background and separators and
 * with a slim scrollbar. Meant to be placed on top of a panel.
 * <p>
 * The lists of 1.21.1 only support one height for all rows.
 */
public abstract class ThemedContainerList<E extends ThemedContainerList.Entry<E>> extends ContainerObjectSelectionList<E> {
    private Component emptyMessage;

    public ThemedContainerList(int itemHeight) {
        super(Minecraft.getInstance(), 0, 0, 0, itemHeight);
    }

    /** Text shown in the list while it has no entries. */
    public void setEmptyMessage(Component emptyMessage) {
        this.emptyMessage = emptyMessage;
    }

    public E getHoveredEntry() {
        return this.getHovered();
    }

    public void updateSizeAndPosition(int width, int height, int x, int y) {
        this.setRectangle(width, height, x, y);
        this.clampScrollAmount();
    }

    /** @return true if the entries don't fit and the list can be scrolled */
    public boolean scrollable() {
        return this.getMaxScroll() > 0;
    }

    // the vanilla scrollbar is replaced by the one drawn in renderDecorations
    @Override
    protected boolean scrollbarVisible() {
        return false;
    }

    @Override
    protected void renderDecorations(GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.scrollable()) {
            ThemedLists.renderScrollbar(graphics, this, this.getScrollbarPosition(), this.getScrollAmount(),
                    this.getMaxScroll(), this.getMaxPosition(), mouseX, mouseY);
        }
        ThemedLists.renderEmptyMessage(graphics, this, this.children().isEmpty() ? this.emptyMessage : null);
    }

    @Override
    protected void renderListBackground(GuiGraphics graphics) {}

    @Override
    protected void renderListSeparators(GuiGraphics graphics) {}

    // matches the area vanilla uses to find the entry below the mouse
    @Override
    public int getRowLeft() {
        return this.getX() + this.width / 2 - this.getRowWidth() / 2;
    }

    @Override
    public int getRowWidth() {
        return this.width - 4 - (this.scrollable() ? 2 * (ThemedLists.SCROLLBAR_WIDTH + 2) : 0);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getRight() - ThemedLists.SCROLLBAR_WIDTH - 1;
    }

    /** Entry that remembers its bounds of the last frame, the vanilla entries of 1.21.1 don't know them. */
    public abstract static class Entry<E extends Entry<E>> extends ContainerObjectSelectionList.Entry<E> {
        private int x, y, width, height;

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        @Override
        public final void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
                                 int mouseX, int mouseY, boolean hovered, float partialTick) {
            this.x = left;
            this.y = top;
            this.width = width;
            // vanilla passes the height without the gap of 4 pixels between the rows
            this.height = height + 4;
            this.renderContent(graphics, mouseX, mouseY, hovered, partialTick);
        }

        public abstract void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float partialTick);
    }
}
