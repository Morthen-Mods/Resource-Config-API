package net.morthen.resourceconfigapi.client.ui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.morthen.resourceconfigapi.client.ui.Themes;
import net.morthen.resourceconfigapi.client.ui.UiUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Dropdown menu. The open menu overlaps other widgets, so the owning screen has to draw it on a
 * later stratum and route the input to it first, {@link PopupLayer} does both.
 * The menu opens below the button and flips upwards if there isn't enough room.
 */
public class ThemedDropdown<T> extends AbstractButton implements PopupWidget {
    private static final int SCREEN_MARGIN = 4;
    private static final int MENU_PADDING = 2;

    private final Minecraft client = Minecraft.getInstance();
    private final List<T> options;

    private Function<T, Component> displayName = t -> Component.literal(t.toString());
    private Consumer<T> onSelect;
    private int rowHeight = 14;
    private int maxVisibleRows = 6;

    private boolean open;
    private boolean openUpwards;
    private int selected;
    private int highlighted;
    private int scrollOffset;

    public ThemedDropdown(List<T> options, int initialIndex) {
        super(0, 0, 100, 18, Component.empty());
        this.options = options;
        this.selected = initialIndex;
        this.highlighted = initialIndex;
        this.setMessage(this.displayName.apply(options.get(this.selected)));
    }

    public T getSelected() {
        return this.options.get(this.selected);
    }

    public void setSelected(T value) {
        this.select(this.options.indexOf(value));
    }

    /** Called with the new value whenever another option gets selected. */
    public void setOnSelect(Consumer<T> onSelect) {
        this.onSelect = onSelect;
    }

    public void setDisplayName(Function<T, Component> displayName) {
        this.displayName = displayName;
        this.setMessage(displayName.apply(this.getSelected()));
    }

    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }

    public void setMaxVisibleRows(int maxVisibleRows) {
        this.maxVisibleRows = maxVisibleRows;
    }

    /** Closes the menu and selects the option, invalid indices only close it. */
    private void select(int index) {
        this.open = false;

        if (index >= 0 && index < this.options.size() && index != this.selected) {
            this.selected = index;
            this.setMessage(this.displayName.apply(this.options.get(index)));

            if (this.onSelect != null) this.onSelect.accept(this.options.get(index));
        }
    }

    // --------------------------------------------------------------------------------------------
    // Layout
    // --------------------------------------------------------------------------------------------

    private int rowsThatFit(int space) {
        return Math.max(1, (space - 2 * MENU_PADDING) / this.rowHeight);
    }

    private int visibleRows() {
        int wanted = Math.min(this.options.size(), this.maxVisibleRows);
        if (this.client.gui.screen() == null) return wanted;

        int space = this.openUpwards ? this.getY() - SCREEN_MARGIN : this.client.gui.screen().height - this.getBottom() - SCREEN_MARGIN;
        return Math.min(wanted, this.rowsThatFit(space));
    }

    /** Decides whether the menu has to open upwards because there's not enough room below. */
    private void updateDirection() {
        if (this.client.gui.screen() == null) {
            this.openUpwards = false;
            return;
        }

        int wanted = Math.min(this.options.size(), this.maxVisibleRows);
        int below = this.rowsThatFit(this.client.gui.screen().height - this.getBottom() - SCREEN_MARGIN);
        int above = this.rowsThatFit(this.getY() - SCREEN_MARGIN);
        this.openUpwards = below < wanted && above > below;
    }

    private int getMenuHeight() {
        return this.visibleRows() * this.rowHeight + 2 * MENU_PADDING;
    }

    private int getMenuTop() {
        return this.openUpwards ? this.getY() - this.getMenuHeight() + 1 : this.getBottom() - 1;
    }

    private boolean isOverMenu(double mouseX, double mouseY) {
        return this.open && UiUtil.inBound(this.getX(), this.getMenuTop(), this.getRight(), this.getMenuTop() + this.getMenuHeight(), mouseX, mouseY);
    }

    private void ensureVisible(int index) {
        this.scrollOffset = Math.clamp(this.scrollOffset, index - this.visibleRows() + 1, index);
    }

    // --------------------------------------------------------------------------------------------
    // Input
    // --------------------------------------------------------------------------------------------

    @Override
    public void onPress(InputWithModifiers input) {
        if (input instanceof MouseButtonEvent mouse && this.isOverMenu(mouse.x(), mouse.y())) {
            int visibleRow = (int) ((mouse.y() - this.getMenuTop() - MENU_PADDING) / this.rowHeight);
            this.select(this.scrollOffset + Math.clamp(visibleRow, 0, this.visibleRows() - 1));
        } else if (this.open && input instanceof KeyEvent) {
            this.select(this.highlighted);
        } else {
            this.open = !this.open;
            this.updateDirection();
            this.highlighted = this.selected;
            this.ensureVisible(this.selected);
        }
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    @Override
    public void close() {
        this.open = false;
    }

    @Override
    public boolean isMouseOverPopup(double mouseX, double mouseY) {
        return this.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void popupClicked(MouseButtonEvent event) {
        this.playDownSound(this.client.getSoundManager());
        this.onPress(event);
    }

    @Override
    public boolean popupScrolled(double mouseX, double mouseY, double scrollY) {
        return this.mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || (this.isActive() && this.isOverMenu(mouseX, mouseY));
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.open && event.key() == InputConstants.KEY_DOWN) {
            this.highlighted = Math.min(this.highlighted + 1, this.options.size() - 1);
            this.ensureVisible(this.highlighted);
            return true;
        }

        if (this.open && event.key() == InputConstants.KEY_UP) {
            this.highlighted = Math.max(this.highlighted - 1, 0);
            this.ensureVisible(this.highlighted);
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (this.isOverMenu(x, y)) {
            this.scrollOffset = Math.clamp(this.scrollOffset - (int) Math.signum(scrollY), 0,
                    this.options.size() - this.visibleRows());
            return true;
        }

        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    // --------------------------------------------------------------------------------------------
    // Rendering
    // --------------------------------------------------------------------------------------------

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int left = this.getX(), top = this.getY(), right = this.getRight(), bottom = this.getBottom();
        Themes.get().control(graphics, left, top, right, bottom, this.isHovered() || this.open, this.isFocused(), this.active);

        int color = this.active ? Themes.get().text : Themes.get().textDisabled;
        int textY = UiUtil.centerY(top, bottom, 8);
        graphics.text(this.client.font, UiUtil.ellipsize(this.client.font, this.getMessage(), this.width - 22), left + 6, textY, color);

        // separator and chevron
        graphics.fill(right - 15, top + 3, right - 14, bottom - 3, Themes.get().controlBorder);
        Themes.get().chevron(graphics, right - 10, UiUtil.centerY(top, bottom, 3), this.open && !this.openUpwards, color);
    }

    @Override
    public void extractPopup(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int left = this.getX(), right = this.getRight();
        int top = this.getMenuTop(), bottom = top + this.getMenuHeight();

        // background with a slight drop shadow
        graphics.fill(left + 2, top + 2, right + 2, bottom + 2, 0x60000000);
        Themes.get().popup(graphics, left, top, right, bottom);

        boolean scrollable = this.options.size() > this.visibleRows();
        int rowRight = right - MENU_PADDING - (scrollable ? 4 : 0);

        for (int row = 0; row < this.visibleRows(); row++) {
            int index = this.scrollOffset + row;
            int rowTop = top + MENU_PADDING + row * this.rowHeight;
            int rowBottom = rowTop + this.rowHeight;

            boolean hovered = UiUtil.inBound(left, rowTop, right, rowBottom, mouseX, mouseY);
            if (hovered || index == this.highlighted) {
                graphics.fill(left + MENU_PADDING, rowTop, rowRight, rowBottom, hovered ? 0x30FFFFFF : 0x1CFFFFFF);
            }

            boolean isSelected = index == this.selected;
            if (isSelected) {
                graphics.fill(left + MENU_PADDING, rowTop + 2, left + MENU_PADDING + 2, rowBottom - 2, Themes.get().accent);
            }

            Component name = this.displayName.apply(this.options.get(index));
            int color = isSelected ? Themes.get().accent : (hovered ? Themes.get().textHighlighted : Themes.get().text);
            int maxWidth = rowRight - (left + 8) - 2;
            graphics.text(this.client.font, UiUtil.ellipsize(this.client.font, name, maxWidth), left + 8, UiUtil.centerY(rowTop, rowBottom, 8), color);
        }

        if (scrollable) this.extractScrollBar(graphics, top, bottom);

        // the screen hides the mouse from the widgets below while the menu is open, so the cursor is set here
        if (this.isMouseOver(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }
    }

    private void extractScrollBar(GuiGraphicsExtractor graphics, int top, int bottom) {
        int trackTop = top + MENU_PADDING;
        int trackHeight = bottom - top - 2 * MENU_PADDING;
        int thumbHeight = Math.max(8, trackHeight * this.visibleRows() / this.options.size());
        int maxScroll = this.options.size() - this.visibleRows();
        int thumbTop = trackTop + (trackHeight - thumbHeight) * this.scrollOffset / maxScroll;

        Themes.get().scrollbar(graphics, this.getRight() - 6, trackTop, 4, trackHeight, thumbTop, thumbHeight, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
