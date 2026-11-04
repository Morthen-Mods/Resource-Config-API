package net.morthen.resourceconfigapi.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.morthen.resourceconfigapi.api.ConfigFile;
import net.morthen.resourceconfigapi.client.screen.entry.ValueEntryFactory;
import net.morthen.resourceconfigapi.client.screen.entry.ValueListEntry;
import net.morthen.resourceconfigapi.client.screen.entry.ValueListRow;
import net.morthen.resourceconfigapi.client.ui.Themes;
import net.morthen.resourceconfigapi.client.ui.UiUtil;
import net.morthen.resourceconfigapi.client.ui.list.ThemedObjectList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Sidebar of a {@link ConfigTab}, one entry per config file. */
public class ConfigListWidget extends ThemedObjectList<ConfigListWidget.Entry> {
    private final Consumer<Entry> onSelect;

    /** @param onSelect called when another config gets selected */
    public ConfigListWidget(Consumer<Entry> onSelect) {
        super(20);
        this.onSelect = onSelect;
    }

    public void addEntry(ConfigFile config) {
        super.addEntry(new Entry(config));
    }

    public void selectFirst() {
        if (!this.children().isEmpty()) this.setSelected(this.children().getFirst());
    }

    @Override
    public void setSelected(Entry entry) {
        Entry previous = this.getSelected();
        super.setSelected(entry);

        if (entry != null && entry != previous) this.onSelect.accept(entry);
    }

    /** Sidebar entry of a config, it holds the value rows of the config while the screen is open. */
    public class Entry extends ThemedObjectList.Entry<Entry> {
        private final Font font = Minecraft.getInstance().font;
        private final ConfigFile config;
        private final Component label;
        private final Component tooltip;
        private final List<ValueListRow> rows;

        private Entry(ConfigFile config) {
            this.config = config;
            this.label = Component.translatable(config.labelKey());
            this.tooltip = Component.translatable(config.tooltipKey());
            this.rows = ValueEntryFactory.create(config);
        }

        public ConfigFile getConfig() {
            return this.config;
        }

        public Component getLabel() {
            return this.label;
        }

        public List<ValueListRow> getRows() {
            return this.rows;
        }

        public boolean hasChanges() {
            return this.rows.stream().anyMatch(ValueListRow::isChanged);
        }

        /**
         * Writes the valid changes of the rows into the options, the file isn't saved yet.
         * @return the keys of the changed options
         */
        public List<String> saveChanges() {
            List<String> keys = new ArrayList<>();
            for (ValueListRow row : this.rows) {
                if (row instanceof ValueListEntry<?> entry && entry.isChanged() && entry.isValid()) {
                    entry.save();
                    keys.add(entry.getOptionKey());
                }
            }
            return keys;
        }

        private boolean isSelected() {
            return ConfigListWidget.this.getSelected() == this;
        }

        @Override
        public Component getNarration() {
            return this.label;
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            int left = this.getX(), top = this.getY() + 1;
            int right = this.getX() + this.getWidth(), bottom = this.getY() + this.getHeight() - 1;
            boolean selected = this.isSelected();

            if (hovered && !selected) {
                Themes.get().fillRounded(graphics, left, top, right, bottom, Themes.get().cardHovered);
            }

            // dot for configs with unsaved changes
            int textRight = right - 6;
            if (this.hasChanges()) {
                int dotY = UiUtil.centerY(top, bottom, 4);
                Themes.get().fillRounded(graphics, right - 9, dotY, right - 5, dotY + 4, Themes.get().changed);
                textRight -= 8;
            }

            int color = selected ? Themes.get().textHighlighted : (hovered ? Themes.get().text : Themes.get().textMuted);
            graphics.drawString(this.font, UiUtil.ellipsize(this.font, this.label, textRight - (left + 8)), left + 8, UiUtil.centerY(top, bottom, 8), color);
        }

        public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
            if (ComponentUtils.isTranslationResolvable(this.tooltip)) {
                Themes.get().tooltip(graphics, this.font, mouseX, mouseY, List.of(this.tooltip));
            }
        }
    }
}
