package net.morthen.resourceconfigapi.client.ui.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.network.chat.Component;

/**
 * List whose entries contain widgets, without the vanilla background and separators and
 * with a slim scrollbar. Meant to be placed on top of a panel.
 * <p>
 * The lists of 1.20.1 only support one height for all rows, and they aren't widgets,
 * the screen has to add them with {@code addRenderableWidget} itself.
 */
public abstract class ThemedContainerList<E extends ThemedContainerList.Entry<E>> extends ContainerObjectSelectionList<E> {
    private Component emptyMessage;

    public ThemedContainerList(int itemHeight) {
        super(Minecraft.getInstance(), 0, 0, 0, 0, itemHeight);
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
    }

    /** Text shown in the list while it has no entries. */
    public void setEmptyMessage(Component emptyMessage) {
        this.emptyMessage = emptyMessage;
    }

    public E getHoveredEntry() {
        return this.getHovered();
    }

    public void updateSizeAndPosition(int width, int height, int x, int y) {
        this.updateSize(width, y + height, y, y + height);
        this.setLeftPos(x);
        this.setScrollAmount(this.getScrollAmount());
    }

    /** @return true if the entries don't fit and the list can be scrolled */
    public boolean scrollable() {
        return this.getMaxScroll() > 0;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // the vanilla scrollbar can't be turned off, so it is cut away and replaced by the themed one
        graphics.enableScissor(this.x0, this.y0, this.getScrollbarPosition(), this.y1);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.disableScissor();

        if (this.scrollable()) {
            ThemedLists.renderScrollbar(graphics, this.getScrollbarPosition(), this.y0, this.y1, this.getScrollAmount(),
                    this.getMaxScroll(), this.getMaxPosition(), mouseX, mouseY);
        }
        ThemedLists.renderEmptyMessage(graphics, this.x0, this.y0, this.width, this.children().isEmpty() ? this.emptyMessage : null);
    }

    // matches the area vanilla uses to find the entry below the mouse
    @Override
    public int getRowLeft() {
        return this.x0 + this.width / 2 - this.getRowWidth() / 2;
    }

    @Override
    public int getRowWidth() {
        return this.width - 4 - (this.scrollable() ? 2 * (ThemedLists.SCROLLBAR_WIDTH + 2) : 0);
    }

    // vanilla only finds entries left of the scrollbar, without one the whole width is used
    @Override
    protected int getScrollbarPosition() {
        return this.scrollable() ? this.x1 - ThemedLists.SCROLLBAR_WIDTH - 1 : this.x1;
    }

    /** Entry that remembers its bounds of the last frame, the vanilla entries of 1.20.1 don't know them. */
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
