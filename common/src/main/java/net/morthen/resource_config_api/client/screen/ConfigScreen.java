package net.morthen.resource_config_api.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.morthen.resource_config_api.api.ConfigRegistry;
import net.morthen.resource_config_api.api.ConfigType;
import net.morthen.resource_config_api.api.ModConfig;
import net.morthen.resource_config_api.client.ClientConfigSync;
import net.morthen.resource_config_api.client.widget.DropdownWidget;
import net.morthen.resource_config_api.service.Services;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final TabManager tabManager = new TabManager(this::addRenderableWidget, this::removeWidget);
    private final List<ConfigTab> tabs = new ArrayList<>();
    private TabNavigationBar navBar;

    public ConfigScreen(Screen parent, String modId) {
        super(Component.literal(Services.PLATFORM.getModName(modId)));
        this.parent = parent;

        for (ConfigType type : ConfigType.values()) {
            List<ModConfig> configs = ConfigRegistry.all().stream()
                    .filter(config -> config.modId().equals(modId) && config.type() == type)
                    .toList();

            if (!configs.isEmpty()) {
                Component title = Component.translatable("config_api.tab." + type);
                tabs.add(new ConfigTab(title, configs, ClientConfigSync.canEdit(type)));
            }
        }
    }

    @Override
    protected void init() {
        navBar = TabNavigationBar.builder(tabManager, width).addTabs(tabs.toArray(Tab[]::new)).build();
        for (int i = 0; i < tabs.size(); i++) {
            if (!tabs.get(i).isEditable()) {
                navBar.setTabTooltip(i, Tooltip.create(Component.translatable("config_api.read_only")));
            }
        }
        addRenderableWidget(navBar);

        LinearLayout footer = layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footer.addChild(Button.builder(Component.translatable("config_api.button.save"), button -> saveAndClose()).build());
        footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose()).build());
        layout.visitWidgets(this::addRenderableWidget);

        if (!tabs.isEmpty()) navBar.selectTab(0, false);
        repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (navBar == null) return;

        navBar.updateWidth(width);
        navBar.arrangeElements();
        int top = navBar.getRectangle().bottom();
        tabManager.setTabArea(new ScreenRectangle(0, top, width, height - layout.getFooterHeight() - top));
        layout.arrangeElements();
    }

    private void saveAndClose() {
        for (ConfigTab tab : tabs) {
            tab.getChanges().forEach(ClientConfigSync::submit);
        }
        onClose();
    }

    public void refresh(ModConfig config) {
        tabs.forEach(tab -> tab.refresh(config));
    }

    private DropdownWidget<?> getOpenDropdown() {
        return tabManager.getCurrentTab() instanceof ConfigTab tab ? tab.getOpenDropdown() : null;
    }

    // An open dropdown gets all input until it closes, its menu covers the rows below it.

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        DropdownWidget<?> dropdown = getOpenDropdown();
        if (dropdown == null) return super.mouseClicked(event, doubleClick);

        if (!dropdown.mouseClicked(event, doubleClick)) dropdown.close();
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        DropdownWidget<?> dropdown = getOpenDropdown();
        if (dropdown == null) return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

        if (!dropdown.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) dropdown.close();
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        DropdownWidget<?> dropdown = getOpenDropdown();
        if (dropdown == null) return super.keyPressed(event);

        if (event.isEscape() || !dropdown.keyPressed(event)) dropdown.close();
        return true;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        DropdownWidget<?> dropdown = getOpenDropdown();
        boolean overMenu = dropdown != null && dropdown.isMouseOver(mouseX, mouseY);
        super.extractRenderState(graphics, overMenu ? -1 : mouseX, overMenu ? -1 : mouseY, a);

        graphics.blit(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR, 0, height - layout.getFooterHeight() - 2,
                0, 0, width, 2, 32, 2);

        if (dropdown != null) {
            graphics.nextStratum();
            dropdown.extractMenu(graphics, mouseX, mouseY);
        }
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }
}
