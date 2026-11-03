package net.morthen.resourceconfigapi.client.screen.entry;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.morthen.resourceconfigapi.api.ConfigOption;
import net.morthen.resourceconfigapi.api.Range;
import net.morthen.resourceconfigapi.client.ui.widget.ThemedSlider;

import java.util.Locale;
import java.util.function.DoubleFunction;

/** Slider entry for numbers with a range. */
public class SliderValueEntry<T extends Number> extends ValueListEntry<T> {
    private final ThemedSlider slider;
    private final DoubleFunction<T> converter;

    /**
     * @param decimal whole numbers snap to steps of 1, decimals to 0.1 (or 0.01 for ranges up to 1)
     * @param converter converts the slider value into the option type
     */
    public SliderValueEntry(ConfigOption<?> option, Range range, boolean decimal, DoubleFunction<T> converter) {
        super(option);
        this.converter = converter;

        double step = decimal ? (range.max() > 1 ? 0.1 : 0.01) : 1;
        this.slider = new ThemedSlider(100, 18, range.min(), range.max(), step,
                value -> Component.literal(decimal ? String.format(Locale.ROOT, "%.2f", value) : String.valueOf(Math.round(value))));
        this.slider.setRealValue(this.option.get().doubleValue());
        this.addChild(this.slider);
    }

    @Override
    protected AbstractWidget getControl() {
        return this.slider;
    }

    @Override
    protected T getWidgetValue() {
        return this.converter.apply(this.slider.getRealValue());
    }

    @Override
    protected void setWidgetValue(T value) {
        this.slider.setRealValue(value.doubleValue());
    }
}
