package net.morthen.resource_config_api.client.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.morthen.resource_config_api.api.ModConfig;
import net.morthen.resource_config_api.client.widget.DropdownWidget;

import java.util.ArrayList;
import java.util.List;

public class ValueList extends ContainerObjectSelectionList<Row> {
    public ValueList() {
        super(Minecraft.getInstance(), 0, 0, 0, 24);
    }

    public static List<Row> createRows(ModConfig config, boolean editable) {
        List<Row> rows = new ArrayList<>();
        String category = null;

        for (String key : config.keys()) {
            String entryCategory = config.entry(key).category();
            if (!entryCategory.equals(category)) {
                category = entryCategory;
                rows.add(new CategoryRow(categoryLabel(config, category)));
            }
            rows.add(new ValueRow(config, key, editable));
        }
        return rows;
    }

    private static Component categoryLabel(ModConfig config, String category) {
        if (category.isBlank()) return Component.translatable("config_api.category.general");
        return Component.translatableWithFallback(config.translationKey() + "." + category + ".label", category);
    }

    public void show(List<Row> rows) {
        replaceEntries(rows);
        setScrollAmount(0);
    }

    public DropdownWidget<?> getOpenDropdown() {
        for (Row row : children()) {
            if (row instanceof ValueRow valueRow && valueRow.getWidget() instanceof DropdownWidget<?> dropdown && dropdown.isOpen()) {
                return dropdown;
            }
        }
        return null;
    }

    @Override
    public int getRowWidth() {
        return width - 12;
    }
}
