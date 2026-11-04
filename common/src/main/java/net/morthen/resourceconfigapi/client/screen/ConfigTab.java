package net.morthen.resourceconfigapi.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.morthen.resourceconfigapi.api.ConfigFile;
import net.morthen.resourceconfigapi.client.screen.entry.ValueListRow;
import net.morthen.resourceconfigapi.client.ui.Themes;
import net.morthen.resourceconfigapi.client.ui.UiUtil;
import net.morthen.resourceconfigapi.client.ui.widget.PopupWidget;
import net.morthen.resourceconfigapi.client.ui.widget.ThemedEditBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Tab with a sidebar listing the config files on the left and
 * a panel with the values of the selected file on the right.
 */
public class ConfigTab implements Tab {
    private static final Component SIDEBAR_TITLE = Component.translatable("config.resourceconfigapi.screen.files");
    private static final Component SEARCH_HINT = Component.translatable("config.resourceconfigapi.screen.search").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);

    private static final int PADDING = 8;
    private static final int PANEL_GAP = 6;
    private static final int HEADER_HEIGHT = 26;

    private final Font font = Minecraft.getInstance().font;
    private final Component title;
    private final boolean editable;
    private final Component note;

    private final ValueListWidget valueList = new ValueListWidget();
    private final ConfigListWidget configList = new ConfigListWidget(entry -> this.valueList.setEntries(entry.getRows()));
    private final ThemedEditBox searchBox;
    private final FrameLayout layout = new FrameLayout();

    private ScreenRectangle sidebar = ScreenRectangle.empty();
    private ScreenRectangle content = ScreenRectangle.empty();

    /**
     * @param editable false to show the values read only
     * @param note shown next to the name of the selected config, null for none
     */
    public ConfigTab(Component title, List<ConfigFile> configs, boolean editable, Component note) {
        this.title = title;
        this.editable = editable;
        this.note = note;

        this.searchBox = new ThemedEditBox(this.font, 120, 18, SEARCH_HINT);
        this.searchBox.setHint(SEARCH_HINT);
        this.searchBox.setResponder(this.valueList::setFilter);

        configs.forEach(this.configList::addEntry);
        this.configList.selectFirst();
        this.getEntries().forEach(entry -> entry.getRows().forEach(row -> row.setEditable(editable)));
    }

    public boolean isEditable() {
        return this.editable;
    }

    /** @return the sidebar entries, one per config with its rows */
    public List<ConfigListWidget.Entry> getEntries() {
        return this.configList.children();
    }

    /** @return the config shown in the value panel, or null if the tab has no configs */
    public ConfigListWidget.Entry getSelectedConfig() {
        return this.configList.getSelected();
    }

    /** @return the popup widgets of the entries currently shown in the value list */
    public List<PopupWidget> getPopups() {
        List<PopupWidget> popups = new ArrayList<>();
        for (ValueListRow row : this.valueList.children()) {
            if (row.getPopup() != null) popups.add(row.getPopup());
        }
        return popups;
    }

    // --------------------------------------------------------------------------------------------
    // Tab
    // --------------------------------------------------------------------------------------------

    @Override
    public Component getTabTitle() {
        return this.title;
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> childrenConsumer) {
        childrenConsumer.accept(this.configList);
        childrenConsumer.accept(this.searchBox);
        childrenConsumer.accept(this.valueList);
    }

    @Override
    public void doLayout(ScreenRectangle rec) {
        int top = rec.top() + PADDING;
        int bottom = rec.bottom() - PADDING;
        int sidebarWidth = Mth.clamp(rec.width() / 4, 90, 150);

        this.sidebar = new ScreenRectangle(rec.left() + PADDING, top, sidebarWidth, bottom - top);
        int contentX = this.sidebar.right() + PANEL_GAP;
        this.content = new ScreenRectangle(contentX, top, rec.right() - PADDING - contentX, bottom - top);

        this.configList.updateSizeAndPosition(this.sidebar.width() - 4, this.sidebar.height() - 22, this.sidebar.left() + 2, top + 20);

        int searchWidth = Mth.clamp(this.content.width() / 3, 80, 160);
        this.searchBox.setRectangle(searchWidth, 18, this.content.right() - searchWidth - 4, top + 4);

        this.valueList.updateSizeAndPosition(this.content.width() - 4, this.content.height() - HEADER_HEIGHT - 2, contentX + 2, top + HEADER_HEIGHT);

        this.layout.setPosition(rec.left(), rec.top());
        this.layout.setMinDimensions(rec.width(), rec.height());
    }

    // --------------------------------------------------------------------------------------------
    // Rendering
    // --------------------------------------------------------------------------------------------

    /** Draws the panels behind the lists, called by the screen before the widgets are drawn. */
    public void renderPanels(GuiGraphics graphics) {
        ScreenRectangle s = this.sidebar;
        Themes.get().panel(graphics, s.left(), s.top(), s.right(), s.bottom());
        graphics.drawString(this.font, SIDEBAR_TITLE, s.left() + 8, s.top() + 7, Themes.get().textMuted, false);

        ScreenRectangle c = this.content;
        Themes.get().panel(graphics, c.left(), c.top(), c.right(), c.bottom());

        ConfigListWidget.Entry selected = this.configList.getSelected();
        if (selected != null) {
            int maxWidth = this.searchBox.getX() - 8 - (c.left() + 8);
            graphics.drawString(this.font, UiUtil.ellipsize(this.font, this.headerText(selected), maxWidth),
                    c.left() + 8, UiUtil.centerY(c.top(), c.top() + HEADER_HEIGHT, 8), Themes.get().textHighlighted);
        }

        graphics.fill(c.left() + 1, c.top() + HEADER_HEIGHT - 1, c.right() - 1, c.top() + HEADER_HEIGHT, Themes.get().panelBorder);
    }

    private Component headerText(ConfigListWidget.Entry selected) {
        if (this.note == null) return selected.getLabel();
        return selected.getLabel().copy().append(Component.literal("  ")).append(this.note.copy().withStyle(ChatFormatting.GRAY));
    }

    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        ConfigListWidget.Entry config = this.configList.getHoveredEntry();
        if (config != null) config.renderTooltip(graphics, mouseX, mouseY);

        ValueListRow value = this.valueList.getHoveredEntry();
        if (value != null) value.renderTooltip(graphics, mouseX, mouseY);
    }
}
