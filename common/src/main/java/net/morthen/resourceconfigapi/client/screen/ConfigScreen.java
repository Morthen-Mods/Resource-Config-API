package net.morthen.resourceconfigapi.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.morthen.resourceconfigapi.api.ConfigFile;
import net.morthen.resourceconfigapi.api.ConfigRegistry;
import net.morthen.resourceconfigapi.api.ConfigType;
import net.morthen.resourceconfigapi.client.ClientConfigSync;
import net.morthen.resourceconfigapi.client.screen.entry.ValueListRow;
import net.morthen.resourceconfigapi.client.theme.ThemeSettings;
import net.morthen.resourceconfigapi.client.ui.Themes;
import net.morthen.resourceconfigapi.client.ui.screen.ThemedConfirmScreen;
import net.morthen.resourceconfigapi.client.ui.widget.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/** Config screen of a mod with a tab per config type, the footer holds reset, cancel and save. */
public class ConfigScreen extends Screen {
    private static final Component SAVE = Component.translatable("config.resourceconfigapi.screen.save");
    private static final Component THEME = Component.translatable("config.resourceconfigapi.screen.theme");
    private static final Component RESET = Component.translatable("config.resourceconfigapi.screen.reset");
    private static final Component RESET_TOOLTIP = Component.translatable("config.resourceconfigapi.screen.reset.tooltip");
    private static final Component DISCARD = Component.translatable("config.resourceconfigapi.screen.discard");
    private static final Component DISCARD_TITLE = Component.translatable("config.resourceconfigapi.screen.discard.title");
    private static final Component DISCARD_MESSAGE = Component.translatable("config.resourceconfigapi.screen.discard.message");
    private static final Component EMPTY = Component.translatable("config.resourceconfigapi.screen.empty");
    private static final Component SERVER_EDITABLE = Component.translatable("config.resourceconfigapi.screen.server_editable");
    private static final Component SERVER_READ_ONLY = Component.translatable("config.resourceconfigapi.screen.server_read_only");

    private static final int FOOTER_HEIGHT = 36;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_GAP = 8;

    private final Screen parent;
    private final String modId;
    private final List<ConfigTab> tabs = new ArrayList<>();
    private final PopupLayer popups = new PopupLayer(this::getAllPopups);

    private TabManager manager;
    private ThemedTabBar tabBar;
    private int selectedTab = 0;

    private ThemedButton resetButton;
    private ThemedButton cancelButton;
    private ThemedButton saveButton;
    private ThemedDropdown<String> themeDropdown;

    public ConfigScreen(Screen parent, String modId) {
        super(Component.literal("Config Screen - " + modId));
        this.parent = parent;
        this.modId = modId;

        this.addTab(Component.literal("Client"), ConfigType.CLIENT);
        this.addTab(Component.literal("Common"), ConfigType.COMMON);
    }

    @Override
    protected void init() {
        // init() runs again when coming back from the confirm screen, the old manager still holds
        // the current tab and would skip re-adding its widgets, so a fresh one is needed
        this.manager = new TabManager(this::addRenderableWidget, this::removeWidget, this::onTabSelected, this::onTabDeselected);

        this.resetButton = this.addRenderableWidget(ThemedButton.builder(RESET, _ -> this.resetSelectedConfig())
                .tooltip(Themes.get().tooltip(RESET_TOOLTIP)).build());
        this.cancelButton = this.addRenderableWidget(ThemedButton.builder(CommonComponents.GUI_CANCEL, _ -> this.onClose())
                .build());
        this.saveButton = this.addRenderableWidget(ThemedButton.builder(SAVE, _ -> this.saveAndClose())
                .style(ThemedButton.Style.PRIMARY).build());

        List<String> themes = List.copyOf(Themes.ids());
        this.themeDropdown = this.addRenderableWidget(new ThemedDropdown<>(themes, themes.indexOf(Themes.currentId())));
        this.themeDropdown.setDisplayName(id -> Component.translatableWithFallback("config.resourceconfigapi.screen.theme." + id, id));
        this.themeDropdown.setOnSelect(id -> {
            ThemeSettings.select(id);
            // tooltips are created per theme
            this.rebuildWidgets();
        });
        this.themeDropdown.setTooltip(Themes.get().tooltip(THEME));

        if (!this.tabs.isEmpty()) {
            this.tabBar = this.addRenderableWidget(new ThemedTabBar(this.manager, this.tabs, this.width));
            this.tabBar.selectTab(Math.min(this.selectedTab, this.tabs.size() - 1), false);
        }

        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (this.tabBar != null) {
            this.tabBar.updateWidth(this.width);
            int top = this.tabBar.getBottom();
            this.manager.setTabArea(new ScreenRectangle(0, top, this.width, this.height - top - FOOTER_HEIGHT));
        }

        int buttonWidth = Math.min(BUTTON_WIDTH, (this.width - 16 - 2 * BUTTON_GAP) / 3);
        int x = (this.width - 3 * buttonWidth - 2 * BUTTON_GAP) / 2;
        int y = this.height - FOOTER_HEIGHT + 8;

        int themeWidth = Math.min(80, this.width / 5);
        this.themeDropdown.setRectangle(themeWidth, 18, this.width - themeWidth - 8, (ThemedTabBar.HEIGHT - 18) / 2);

        for (ThemedButton button : List.of(this.resetButton, this.cancelButton, this.saveButton)) {
            button.setRectangle(buttonWidth, 20, x, y);
            x += buttonWidth + BUTTON_GAP;
        }
    }

    // --------------------------------------------------------------------------------------------
    // Tabs
    // --------------------------------------------------------------------------------------------

    /** Only adds the tab if the mod has at least one config of this type. */
    private void addTab(Component title, ConfigType type) {
        List<ConfigFile> configs = ConfigRegistry.getConfigs(this.modId, type);
        if (configs.isEmpty()) return;

        boolean editable = ClientConfigSync.canEdit(type);
        Component note = !ClientConfigSync.usesServerValues(type) ? null : editable ? SERVER_EDITABLE : SERVER_READ_ONLY;
        this.tabs.add(new ConfigTab(title, configs, editable, note));
    }

    private ConfigTab getCurrentTab() {
        return this.manager != null && this.manager.getCurrentTab() instanceof ConfigTab tab ? tab : null;
    }

    private void onTabSelected(Tab tab) {
        // remembered so the tab stays selected when init() runs again
        if (tab instanceof ConfigTab configTab) this.selectedTab = this.tabs.indexOf(configTab);
    }

    private void onTabDeselected(Tab tab) {
        if (tab instanceof ConfigTab configTab) configTab.getPopups().forEach(PopupWidget::close);
    }

    private List<PopupWidget> getAllPopups() {
        List<PopupWidget> all = new ArrayList<>();
        all.add(this.themeDropdown);
        if (this.getCurrentTab() != null) all.addAll(this.getCurrentTab().getPopups());
        return all;
    }

    /** @return the sidebar entries of all tabs, one per config */
    private Stream<ConfigListWidget.Entry> entries() {
        return this.tabs.stream().flatMap(tab -> tab.getEntries().stream());
    }

    // --------------------------------------------------------------------------------------------
    // Saving
    // --------------------------------------------------------------------------------------------

    private boolean hasChanges() {
        return this.entries().anyMatch(ConfigListWidget.Entry::hasChanges);
    }

    private void saveAndClose() {
        for (ConfigTab tab : this.tabs) {
            if (!tab.isEditable()) continue;

            for (ConfigListWidget.Entry entry : tab.getEntries()) {
                List<String> changedKeys = entry.saveChanges();
                if (!changedKeys.isEmpty()) ClientConfigSync.submit(entry.getConfig(), changedKeys);
            }
        }

        this.minecraft.setScreen(this.parent);
    }

    /**
     * Runs a change of the config that didn't come from this screen, like values sent by the server.
     * Entries the user is editing keep their input, all others show the new values.
     */
    public void applyExternalChange(ConfigFile config, Runnable change) {
        List<ValueListRow> untouched = this.entries()
                .filter(entry -> entry.getConfig() == config)
                .flatMap(entry -> entry.getRows().stream())
                .filter(row -> !row.isChanged())
                .toList();

        change.run();
        untouched.forEach(ValueListRow::undo);
    }

    private void resetSelectedConfig() {
        ConfigTab tab = this.getCurrentTab();
        if (tab == null || !tab.isEditable()) return;

        ConfigListWidget.Entry config = tab.getSelectedConfig();
        if (config != null) config.getRows().forEach(ValueListRow::reset);
    }

    @Override
    public void onClose() {
        if (!this.hasChanges()) {
            this.minecraft.setScreen(this.parent);
            return;
        }

        this.minecraft.setScreen(new ThemedConfirmScreen(discard -> {
            if (discard) this.minecraft.setScreen(this.parent);
            else this.minecraft.setScreen(this);
        }, DISCARD_TITLE, DISCARD_MESSAGE)
                .confirmButton(DISCARD, ThemedButton.Style.DANGER)
                .cancelButton(CommonComponents.GUI_BACK));
    }

    // --------------------------------------------------------------------------------------------
    // Input, an open popup gets the input first
    // --------------------------------------------------------------------------------------------

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return this.popups.mouseClicked(event) || super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.popups.keyPressed(event)) return true;
        if (this.tabBar != null && !this.popups.isOpen() && this.tabBar.keyPressed(event)) return true;
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        return this.popups.mouseScrolled(x, y, scrollY) || super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        return this.popups.mouseDragged(event) || super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return this.popups.mouseReleased(event) || super.mouseReleased(event);
    }

    // --------------------------------------------------------------------------------------------
    // Rendering
    // --------------------------------------------------------------------------------------------

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        ConfigTab tab = this.getCurrentTab();
        boolean changes = this.hasChanges();
        this.saveButton.active = changes;
        this.cancelButton.setMessage(changes ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_DONE);
        this.resetButton.active = tab != null && tab.isEditable();

        // footer
        int footerTop = this.height - FOOTER_HEIGHT;
        graphics.fill(0, footerTop, this.width, this.height, Themes.get().header);
        graphics.fill(0, footerTop, this.width, footerTop + 1, Themes.get().divider);

        if (tab != null) {
            tab.extractPanels(graphics);
        } else {
            graphics.centeredText(this.font, EMPTY, this.width / 2, this.height / 2 - 4, Themes.get().textMuted);
        }

        // while a popup is open every click outside of it only closes it, so the widgets below
        // get a mouse position off screen, otherwise they'd show hover effects and change the cursor
        boolean blocked = this.popups.isOpen();
        super.extractRenderState(graphics, blocked ? -1 : mouseX, blocked ? -1 : mouseY, a);

        if (blocked) {
            this.popups.extract(graphics, mouseX, mouseY);
        } else if (tab != null) {
            graphics.nextStratum();
            tab.extractTooltip(graphics, mouseX, mouseY);
        }
    }
}
