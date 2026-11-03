package net.morthen.resourceconfigapi.client.ui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.morthen.resourceconfigapi.client.ui.Themes;

import java.util.function.Consumer;

/**
 * Animated on/off switch as replacement for the vanilla checkbox.
 * The knob uses the vanilla button gray and bevel, so it still fits the game.
 */
public class ThemedToggle extends AbstractButton {
    private static final float ANIMATION_SPEED = 0.012f;

    private boolean value;
    private float progress;
    private long lastFrame = Util.getMillis();

    private Consumer<Boolean> responder;

    public ThemedToggle(boolean value) {
        super(0, 0, 28, 14, CommonComponents.optionStatus(value));
        this.value = value;
        this.progress = value ? 1 : 0;
    }

    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean value) {
        this.value = value;
        this.setMessage(CommonComponents.optionStatus(value));
    }

    /** Called with the new value whenever the user flips the switch. */
    public void setResponder(Consumer<Boolean> responder) {
        this.responder = responder;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        this.setValue(!this.value);
        if (this.responder != null) this.responder.accept(this.value);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        long now = Util.getMillis();
        float step = (now - this.lastFrame) * ANIMATION_SPEED;
        this.lastFrame = now;
        this.progress = Mth.clamp(this.progress + (this.value ? step : -step), 0, 1);

        int left = this.getX(), top = this.getY(), right = this.getRight(), bottom = this.getBottom();
        int track = ARGB.srgbLerp(this.progress, Themes.get().toggleOff, Themes.get().toggleOn);
        if (!this.active) track = ARGB.greyscale(track);

        // Track
        Themes.get().fillRounded(graphics, left, top, right, bottom, this.isHovered() && this.active ? Themes.get().controlBorderHovered : 0xFF4A4A4A);
        Themes.get().fillInner(graphics, left, top, right, bottom, track);
        int inset = Themes.get().innerInset(left, top, right, bottom);
        graphics.fill(left + 1 + inset, top + 1, right - 1 - inset, top + 2, 0x30000000);

        // Knob
        int knobSize = this.height - 2;
        int knobX = left + 1 + Math.round((this.width - 2 - knobSize) * this.progress);
        Themes.get().knob(graphics, knobX, top + 1, knobX + knobSize, top + 1 + knobSize, this.isHovered(), this.active);

        if (this.isFocused()) {
            Themes.get().outlineRounded(graphics, left - 1, top - 1, right + 1, bottom + 1, Themes.get().controlBorderFocused);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
