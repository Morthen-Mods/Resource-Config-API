package net.morthen.resourceconfigapi.client.ui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.morthen.resourceconfigapi.client.ui.Themes;
import net.morthen.resourceconfigapi.client.ui.UiUtil;

import java.util.List;

/**
 * Header bar with centered text tabs, the selected tab is marked with an accent underline.
 * Drop-in replacement for the vanilla {@code TabNavigationBar}, it drives the same {@link TabManager}.
 * <p>
 * Ctrl+Tab / Ctrl+Shift+Tab and Ctrl+1..9 switch tabs if the screen forwards {@link #keyPressed}
 * before its own handling, the arrow keys work while the bar is focused.
 */
public class ThemedTabBar extends AbstractWidget {
    public static final int HEIGHT = 24;
    private static final int TAB_PADDING = 12;
    private static final int TAB_GAP = 2;

    private final Font font = Minecraft.getInstance().font;
    private final TabManager manager;
    private final List<Tab> tabs;

    private final int[] tabX;
    private final int[] tabWidth;

    public ThemedTabBar(TabManager manager, List<? extends Tab> tabs, int width) {
        super(0, 0, width, HEIGHT, CommonComponents.EMPTY);
        this.manager = manager;
        this.tabs = List.copyOf(tabs);
        this.tabX = new int[this.tabs.size()];
        this.tabWidth = new int[this.tabs.size()];
        this.arrangeElements();
    }

    public void updateWidth(int width) {
        this.setWidth(width);
        this.arrangeElements();
    }

    private void arrangeElements() {
        int total = TAB_GAP * Math.max(0, this.tabs.size() - 1);
        for (int i = 0; i < this.tabs.size(); i++) {
            this.tabWidth[i] = this.font.width(this.tabs.get(i).getTabTitle()) + 2 * TAB_PADDING;
            total += this.tabWidth[i];
        }

        int x = this.getX() + (this.width - total) / 2;
        for (int i = 0; i < this.tabs.size(); i++) {
            this.tabX[i] = x;
            x += this.tabWidth[i] + TAB_GAP;
        }
    }

    public List<Tab> getTabs() {
        return this.tabs;
    }

    public int getSelectedIndex() {
        return this.tabs.indexOf(this.manager.getCurrentTab());
    }

    public void selectTab(int index, boolean playSound) {
        if (index >= 0 && index < this.tabs.size()) {
            this.manager.setCurrentTab(this.tabs.get(index), playSound);
        }
    }

    private int tabAt(double mouseX, double mouseY) {
        if (mouseY < this.getY() || mouseY >= this.getBottom()) return -1;

        for (int i = 0; i < this.tabs.size(); i++) {
            if (mouseX >= this.tabX[i] && mouseX < this.tabX[i] + this.tabWidth[i]) return i;
        }
        return -1;
    }

    // only the tabs themselves are clickable, so clicking the empty bar plays no sound
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.isActive() && this.tabAt(mouseX, mouseY) != -1;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.selectTab(this.tabAt(mouseX, mouseY), false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int selected = this.getSelectedIndex();

        if (Screen.hasControlDown()) {
            if (keyCode >= InputConstants.KEY_0 && keyCode <= InputConstants.KEY_9) {
                this.selectTab(Math.floorMod(keyCode - InputConstants.KEY_0 - 1, 10), true);
                return true;
            }
            if (keyCode == InputConstants.KEY_TAB && selected != -1) {
                this.selectTab(Math.floorMod(selected + (Screen.hasShiftDown() ? -1 : 1), this.tabs.size()), true);
                return true;
            }
        }

        if (this.isFocused() && (keyCode == InputConstants.KEY_LEFT || keyCode == InputConstants.KEY_RIGHT)) {
            int next = selected + (keyCode == InputConstants.KEY_LEFT ? -1 : 1);
            if (next >= 0 && next < this.tabs.size()) this.selectTab(next, true);
            return true;
        }

        return false;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int top = this.getY(), bottom = this.getBottom();
        graphics.fill(this.getX(), top, this.getRight(), bottom, Themes.get().header);
        graphics.fill(this.getX(), bottom - 1, this.getRight(), bottom, Themes.get().divider);

        int selected = this.getSelectedIndex();
        int hovered = this.tabAt(mouseX, mouseY);

        for (int i = 0; i < this.tabs.size(); i++) {
            int left = this.tabX[i], right = left + this.tabWidth[i];
            boolean isSelected = i == selected;
            boolean isHovered = i == hovered;

            if (isHovered && !isSelected) {
                Themes.get().fillRounded(graphics, left, top + 3, right, bottom - 3, Themes.get().cardHovered);
            }
            if (isSelected) {
                graphics.fill(left + 4, bottom - 3, right - 4, bottom - 1, Themes.get().accent);
            }

            int color = isSelected ? Themes.get().textHighlighted : (isHovered ? Themes.get().text : Themes.get().textMuted);
            graphics.drawString(this.font, this.tabs.get(i).getTabTitle(), left + TAB_PADDING, UiUtil.centerY(top, bottom - 1, 8), color);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        int selected = this.getSelectedIndex();
        if (selected == -1) return;

        Tab tab = this.tabs.get(selected);
        output.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.tab", tab.getTabTitle()));
        output.add(NarratedElementType.POSITION, Component.translatable("narrator.position.tab", selected + 1, this.tabs.size()));
    }
}
