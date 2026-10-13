package net.morthen.resource_config_api.client.screen;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.morthen.resource_config_api.api.ModConfig;
import net.morthen.resource_config_api.client.list.FileList;
import net.morthen.resource_config_api.client.list.Row;
import net.morthen.resource_config_api.client.list.ValueList;
import net.morthen.resource_config_api.client.list.ValueRow;
import net.morthen.resource_config_api.client.widget.DropdownWidget;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

class ConfigTab implements Tab {
    private final Component title;
    private final boolean editable;
    // kept per file so unsaved edits survive switching between files
    private final Map<ModConfig, List<Row>> rows = new LinkedHashMap<>();
    private final ValueList values = new ValueList();
    private final FileList files;

    ConfigTab(Component title, List<ModConfig> configs, boolean editable) {
        this.title = title;
        this.editable = editable;
        this.files = new FileList(configs, config -> values.show(getRows(config)));
        files.setSelected(files.children().getFirst());
    }

    boolean isEditable() {
        return editable;
    }

    Map<ModConfig, Map<String, Object>> getChanges() {
        Map<ModConfig, Map<String, Object>> changes = new LinkedHashMap<>();
        rows.forEach((config, configRows) -> {
            Map<String, Object> configChanges = new LinkedHashMap<>();
            for (Row row : configRows) {
                if (row instanceof ValueRow valueRow) valueRow.collectChange(configChanges);
            }
            if (!configChanges.isEmpty()) changes.put(config, configChanges);
        });
        return changes;
    }

    void refresh(ModConfig config) {
        rows.remove(config);
        if (files.getSelectedConfig() == config) values.show(getRows(config));
    }

    DropdownWidget<?> getOpenDropdown() {
        return values.getOpenDropdown();
    }

    private List<Row> getRows(ModConfig config) {
        return rows.computeIfAbsent(config, c -> ValueList.createRows(c, editable));
    }

    @Override
    public Component getTabTitle() {
        return title;
    }

    @Override
    public Component getTabExtraNarration() {
        return title;
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> consumer) {
        consumer.accept(files);
        consumer.accept(values);
    }

    @Override
    public void doLayout(ScreenRectangle area) {
        int filesWidth = Math.clamp(files.getPreferredWidth(), 60, area.width() / 3);
        int top = area.top() + 5;
        int height = area.height() - 10;

        files.updateSizeAndPosition(filesWidth, height, area.left() + 5, top);
        values.updateSizeAndPosition(area.width() - filesWidth - 15, height, area.left() + filesWidth + 10, top);
    }
}
