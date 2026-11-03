package net.morthen.resourceconfigapi.client.screen.entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.morthen.resourceconfigapi.client.ui.widget.PopupWidget;

import java.util.ArrayList;
import java.util.List;

/** Row of the value list, either a category header or an option entry. */
public abstract class ValueListRow extends ContainerObjectSelectionList.Entry<ValueListRow> {
    private final List<AbstractWidget> children = new ArrayList<>();
    protected final Font font = Minecraft.getInstance().font;

    /** @return the height of the row in the list */
    public abstract int getRowHeight();

    /** @return the text used to match this row against the search query */
    public abstract String getSearchText();

    /** @return true if the value in the widget differs from the stored value */
    public boolean isChanged() {
        return false;
    }

    /** @return true if the value in the widget can be saved */
    public boolean isValid() {
        return true;
    }

    /** Writes the value of the widget into the option. */
    public void save() {}

    /** Sets the widget back to the stored value. */
    public void undo() {}

    /** Sets the widget to the default value. */
    public void reset() {}

    /** Disables the widgets of the row, for values the player isn't allowed to change. */
    public void setEditable(boolean editable) {}

    /** @return the popup widget of this row, the screen draws its popup on top of everything else */
    public PopupWidget getPopup() {
        return null;
    }

    public void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {}

    @Override
    public List<? extends NarratableEntry> narratables() {
        return this.children;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.children;
    }

    protected void addChild(AbstractWidget widget) {
        if (!this.children.contains(widget)) this.children.add(widget);
    }
}
