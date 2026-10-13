package net.morthen.resource_config_api.client.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.morthen.resource_config_api.api.RangedEntry;

public class RangedSlider extends AbstractSliderButton {
    private final RangedEntry range;
    private final boolean integral;

    public RangedSlider(RangedEntry range, boolean integral) {
        super(0, 0, 100, 20, Component.empty(), 0);
        this.range = range;
        this.integral = integral;
    }

    public double getActualValue() {
        double actual = Mth.lerp(value, range.minValue(), range.maxValue());
        return integral ? Math.round(actual) : Math.round(actual * 100) / 100.0;
    }

    public void setActualValue(double actual) {
        setValue((actual - range.minValue()) / (range.maxValue() - range.minValue()));
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        double actual = getActualValue();
        setMessage(Component.literal(integral ? String.valueOf((long) actual) : String.format("%.2f", actual)));
    }

    @Override
    protected void applyValue() {}
}
