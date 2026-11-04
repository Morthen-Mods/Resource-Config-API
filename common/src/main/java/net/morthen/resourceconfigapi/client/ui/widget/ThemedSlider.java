package net.morthen.resourceconfigapi.client.ui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.morthen.resourceconfigapi.client.ui.Themes;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;

/**
 * Slider over a real value range with a track, a filled part left of the handle and a beveled knob.
 * <pre>
 * ThemedSlider slider = new ThemedSlider(100, 18, 0, 10, 0.5, value -> Component.literal(value + " blocks"));
 * slider.setRealValue(2.5);
 * slider.setResponder(value -> ...);
 * </pre>
 */
public class ThemedSlider extends AbstractSliderButton {
    /** Width of the handle, the same as the private constant of the vanilla slider. */
    private static final int HANDLE_WIDTH = 8;

    private final double min;
    private final double max;
    private final double step;
    private final DoubleFunction<Component> formatter;
    private DoubleConsumer responder;

    /**
     * @param step the value snaps to multiples of it starting at {@code min}, 0 for no snapping
     * @param formatter creates the text shown on the slider from the current value
     */
    public ThemedSlider(int width, int height, double min, double max, double step, DoubleFunction<Component> formatter) {
        super(0, 0, width, height, Component.empty(), 0);
        this.min = min;
        this.max = max;
        this.step = step;
        this.formatter = formatter;
        this.updateMessage();
    }

    public double getRealValue() {
        double value = Mth.lerp(this.value, this.min, this.max);
        if (this.step > 0) value = this.min + Math.round((value - this.min) / this.step) * this.step;
        return Mth.clamp(value, this.min, this.max);
    }

    public void setRealValue(double value) {
        double old = this.value;
        this.value = Mth.clamp(this.max > this.min ? (value - this.min) / (this.max - this.min) : 0, 0, 1);
        if (old != this.value) this.applyValue();
        this.updateMessage();
    }

    /** Called with the new value whenever the user moves the slider. */
    public void setResponder(DoubleConsumer responder) {
        this.responder = responder;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(this.formatter.apply(this.getRealValue()));
    }

    @Override
    protected void applyValue() {
        if (this.responder != null) this.responder.accept(this.getRealValue());
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int left = this.getX(), top = this.getY(), right = this.getRight(), bottom = this.getBottom();
        boolean highlighted = this.isHoveredOrFocused();

        Themes.get().control(graphics, left, top, right, bottom, this.isHovered(), this.isFocused(), this.active);

        int handleX = left + (int) (this.value * (this.width - HANDLE_WIDTH));
        if (this.active) {
            // the right end is hidden below the knob, so the fill only has to match the border on the left
            Themes.get().fillInner(graphics, left, top, handleX + HANDLE_WIDTH / 2 + 1, bottom, Themes.get().sliderFill);
        }
        Themes.get().knob(graphics, handleX, top, handleX + HANDLE_WIDTH, bottom, highlighted, this.active);

        int color = this.active ? Themes.get().text : Themes.get().textDisabled;
        this.renderScrollingString(graphics, Minecraft.getInstance().font, 2, color);
    }
}
