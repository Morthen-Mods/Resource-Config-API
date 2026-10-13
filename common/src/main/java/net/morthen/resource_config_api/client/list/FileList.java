package net.morthen.resource_config_api.client.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.morthen.resource_config_api.api.ModConfig;

import java.util.List;
import java.util.function.Consumer;

public class FileList extends ObjectSelectionList<FileEntry> {
    private final Consumer<ModConfig> onSelect;

    public FileList(List<ModConfig> configs, Consumer<ModConfig> onSelect) {
        super(Minecraft.getInstance(), 0, 0, 0, 16);
        this.onSelect = onSelect;
        configs.forEach(config -> addEntry(new FileEntry(config)));
    }

    public ModConfig getSelectedConfig() {
        FileEntry selected = getSelected();
        return selected == null ? null : selected.getConfig();
    }

    public int getPreferredWidth() {
        return children().stream().mapToInt(FileEntry::getLabelWidth).max().orElse(0) + 16;
    }

    @Override
    public void setSelected(FileEntry entry) {
        super.setSelected(entry);
        if (entry != null) onSelect.accept(entry.getConfig());
    }

    @Override
    public int getRowWidth() {
        return width - 4;
    }
}
