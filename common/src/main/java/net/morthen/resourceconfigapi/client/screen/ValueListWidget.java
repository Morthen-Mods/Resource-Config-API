package net.morthen.resourceconfigapi.client.screen;

import net.minecraft.network.chat.Component;
import net.morthen.resourceconfigapi.client.screen.entry.CategoryEntry;
import net.morthen.resourceconfigapi.client.screen.entry.ValueListEntry;
import net.morthen.resourceconfigapi.client.screen.entry.ValueListRow;
import net.morthen.resourceconfigapi.client.ui.list.ThemedContainerList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ValueListWidget extends ThemedContainerList<ValueListRow> {
    private static final Component NO_RESULTS = Component.translatable("config.resourceconfigapi.screen.no_results");

    private final List<ValueListRow> allRows = new ArrayList<>();
    private String filter = "";

    public ValueListWidget() {
        super(ValueListEntry.HEIGHT);
        this.setEmptyMessage(NO_RESULTS);
    }

    /** Replaces the shown rows, the current search filter stays active. */
    public void setEntries(List<ValueListRow> rows) {
        this.allRows.clear();
        this.allRows.addAll(rows);
        this.rebuild();
        this.setScrollAmount(0);
    }

    public void setFilter(String filter) {
        String normalized = filter.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals(this.filter)) return;

        this.filter = normalized;
        this.rebuild();
        this.setScrollAmount(0);
    }

    private void rebuild() {
        this.allRows.forEach(row -> {
            if (row.getPopup() != null) row.getPopup().close();
        });
        this.clearEntries();

        // categories are only shown if at least one of their entries matches the filter
        ValueListRow pendingCategory = null;
        for (ValueListRow row : this.allRows) {
            if (row instanceof CategoryEntry) {
                pendingCategory = row;
                continue;
            }

            if (!this.filter.isEmpty() && !row.getSearchText().toLowerCase(Locale.ROOT).contains(this.filter)) continue;

            if (pendingCategory != null) {
                this.addEntry(pendingCategory);
                pendingCategory = null;
            }
            this.addEntry(row);
        }
    }
}
