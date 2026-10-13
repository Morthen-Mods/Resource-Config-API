package net.morthen.resource_config_api.client.widget;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Function;

public class DropdownWidget<T> extends AbstractButton {
    private static final int ROW_HEIGHT = 12;
    private static final int MAX_ROWS = 5;

    private final Minecraft client = Minecraft.getInstance();
    private final List<T> options;
    private final Function<T, Component> displayName;

    private boolean open;
    private int selected;
    private int highlighted;
    private int scrollOffset;

    public DropdownWidget(List<T> options, Function<T, Component> displayName) {
        super(0, 0, 100, 20, Component.empty());
        this.options = options;
        this.displayName = displayName;
        setSelected(options.getFirst());
    }

    public T getSelected() {
        return options.get(selected);
    }

    public void setSelected(T value) {
        selected = Math.max(0, options.indexOf(value));
        setMessage(displayName.apply(getSelected()));
    }

    public boolean isOpen() {
        return open;
    }

    public void close() {
        open = false;
    }

    private int menuTop() {
        return getBottom();
    }

    private int visibleRows() {
        int fitting = (client.getWindow().getGuiScaledHeight() - menuTop() - 2) / ROW_HEIGHT;
        return Math.clamp(fitting, 1, Math.min(MAX_ROWS, options.size()));
    }

    private int menuHeight() {
        return visibleRows() * ROW_HEIGHT + 2;
    }

    private int maxScroll() {
        return options.size() - visibleRows();
    }

    private void scrollTo(int index) {
        scrollOffset = Math.clamp(scrollOffset, Math.max(0, index - visibleRows() + 1), Math.min(index, maxScroll()));
    }

    private void select(int index) {
        open = false;
        setSelected(options.get(index));
    }

    @Override
    public void onPress(InputWithModifiers input) {
        if (open && input instanceof MouseButtonEvent mouse && mouse.y() >= menuTop()) {
            int row = (int) ((mouse.y() - menuTop() - 1) / ROW_HEIGHT);
            select(scrollOffset + Math.min(row, visibleRows() - 1));
        } else if (open && input instanceof KeyEvent) {
            select(highlighted);
        } else {
            open = !open;
            highlighted = selected;
            scrollTo(selected);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || open
                && mouseX >= getX() && mouseX < getRight()
                && mouseY >= menuTop() && mouseY < menuTop() + menuHeight();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (open && (event.key() == InputConstants.KEY_DOWN || event.key() == InputConstants.KEY_UP)) {
            int step = event.key() == InputConstants.KEY_DOWN ? 1 : -1;
            highlighted = Math.clamp(highlighted + step, 0, options.size() - 1);
            scrollTo(highlighted);
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!open || !isMouseOver(mouseX, mouseY)) return false;

        scrollOffset = Math.clamp(scrollOffset - (int) Math.signum(scrollY), 0, maxScroll());
        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractDefaultSprite(graphics);
        extractScrollingStringOverContents(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE),
                getMessage(), 14);

        int textY = getY() + (height - client.font.lineHeight) / 2 + 1;
        graphics.text(client.font, open ? "▲" : "▼", getRight() - 12, textY, active ? 0xFFE0E0E0 : 0xFF707070, true);
    }

    public void extractMenu(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // the window can shrink while the menu is open
        scrollOffset = Math.clamp(scrollOffset, 0, maxScroll());

        graphics.fill(getX(), menuTop(), getRight(), menuTop() + menuHeight(), 0xF0100010);
        graphics.outline(getX(), menuTop(), getWidth(), menuHeight(), 0xFF5B5B66);

        for (int row = 0; row < visibleRows(); row++) {
            int index = scrollOffset + row;
            int rowTop = menuTop() + 1 + row * ROW_HEIGHT;
            boolean hovered = mouseX >= getX() && mouseX < getRight() && mouseY >= rowTop && mouseY < rowTop + ROW_HEIGHT;

            if (hovered || index == highlighted) {
                graphics.fill(getX() + 1, rowTop, getRight() - 1, rowTop + ROW_HEIGHT, 0xFF404048);
            }
            int color = index == highlighted ? 0xFFFFFF55 : 0xFFE0E0E0;
            graphics.text(client.font, displayName.apply(options.get(index)), getX() + 5, rowTop + 2, color);
        }

        if (maxScroll() > 0) {
            int track = menuHeight() - 2;
            int thumb = Math.max(8, track * visibleRows() / options.size());
            int thumbTop = menuTop() + 1 + (track - thumb) * scrollOffset / maxScroll();
            graphics.fill(getRight() - 3, thumbTop, getRight() - 1, thumbTop + thumb, 0xFF8B8B99);
        }
    }
}
