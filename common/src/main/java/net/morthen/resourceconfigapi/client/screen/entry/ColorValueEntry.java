package net.morthen.resourceconfigapi.client.screen.entry;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.morthen.resourceconfigapi.api.ColorMode;
import net.morthen.resourceconfigapi.api.ConfigOption;
import net.morthen.resourceconfigapi.client.ui.widget.PopupWidget;
import net.morthen.resourceconfigapi.client.ui.widget.ThemedColorPicker;
import net.morthen.resourceconfigapi.client.ui.widget.ThemedEditBox;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Hex input next to a color picker swatch, both stay in sync. RGB options store the color
 * without alpha, the picker makes it opaque on its own.
 */
public class ColorValueEntry extends ValueListEntry<Integer> {
    private static final Pattern HEX_INPUT = Pattern.compile("#?[0-9a-fA-F]{0,8}");

    private final boolean alpha;
    private final ThemedColorPicker picker;
    private final ThemedEditBox hexBox;
    private boolean syncing;

    public ColorValueEntry(ConfigOption<?> option) {
        super(option);
        this.alpha = option.colorMode() == ColorMode.ARGB;

        this.picker = new ThemedColorPicker(18, this.option.get(), this.alpha);
        this.hexBox = new ThemedEditBox(this.font, this.alpha ? 66 : 54, 18, Component.empty());
        this.hexBox.setMaxLength(this.alpha ? 9 : 7);
        this.hexBox.setFilter(text -> HEX_INPUT.matcher(text).matches());
        this.hexBox.setValue(this.picker.getHex());

        this.picker.setResponder(color -> this.sync(() -> this.hexBox.setValue(this.picker.getHex())));
        this.hexBox.setResponder(text -> {
            this.hexBox.setError(!this.isValid());
            Integer color = this.parse(text);
            if (color != null) this.sync(() -> this.picker.setColor(color));
        });

        this.addChild(this.hexBox);
        this.addChild(this.picker);
    }

    /** Runs the update without triggering the responder of the other widget again. */
    private void sync(Runnable update) {
        if (this.syncing) return;
        this.syncing = true;
        update.run();
        this.syncing = false;
    }

    /** @return the color in the format of the option, or null if the text isn't a complete hex color */
    private Integer parse(String text) {
        String hex = text.startsWith("#") ? text.substring(1) : text;
        if (hex.length() != 6 && !(this.alpha && hex.length() == 8)) return null;

        int color = Integer.parseUnsignedInt(hex, 16);
        if (!this.alpha) return color;
        return hex.length() == 6 ? 0xFF000000 | color : color;
    }

    @Override
    public boolean isValid() {
        return this.parse(this.hexBox.getValue()) != null;
    }

    @Override
    public PopupWidget getPopup() {
        return this.picker;
    }

    @Override
    protected AbstractWidget getControl() {
        return this.picker;
    }

    @Override
    protected List<AbstractWidget> getControls() {
        return List.of(this.hexBox, this.picker);
    }

    @Override
    protected Integer getWidgetValue() {
        return this.parse(this.hexBox.getValue());
    }

    @Override
    protected void setWidgetValue(Integer value) {
        this.picker.setColor(value);
        this.hexBox.setValue(this.picker.getHex());
    }
}
