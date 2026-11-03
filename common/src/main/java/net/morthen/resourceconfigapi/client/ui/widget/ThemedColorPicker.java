package net.morthen.resourceconfigapi.client.ui.widget;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.morthen.resourceconfigapi.client.ui.Themes;
import net.morthen.resourceconfigapi.client.ui.UiUtil;

import java.util.Locale;
import java.util.function.IntConsumer;

/**
 * Color swatch that opens a picker with a saturation/brightness field, a hue bar and an optional
 * alpha bar. The popup has to be handled by a {@link PopupLayer}.
 * <pre>
 * ThemedColorPicker picker = new ThemedColorPicker(18, 0xFFFF8800, false);
 * picker.setResponder(color -> ...);
 * </pre>
 */
public class ThemedColorPicker extends AbstractButton implements PopupWidget {
    private static final int PADDING = 6;
    private static final int FIELD_WIDTH = 120;
    private static final int FIELD_HEIGHT = 80;
    private static final int BAR_HEIGHT = 8;
    private static final int GAP = 4;
    private static final int PREVIEW_HEIGHT = 12;
    private static final int SCREEN_MARGIN = 4;

    private enum DragTarget { NONE, FIELD, HUE, ALPHA }

    private final Minecraft client = Minecraft.getInstance();
    private final boolean alpha;

    // the exact color is kept, so a round trip through hsv can't change it by rounding
    private int color;
    private float hue;
    private float saturation;
    private float brightness;

    private int colorOnOpen;
    private boolean open;
    private DragTarget dragTarget = DragTarget.NONE;
    private IntConsumer responder;

    /**
     * @param color the initial color as {@code 0xAARRGGBB}
     * @param alpha true to show the alpha bar, otherwise the color is always opaque
     */
    public ThemedColorPicker(int size, int color, boolean alpha) {
        super(0, 0, size, size, Component.empty());
        this.alpha = alpha;
        this.setColor(color);
    }

    /** @return the color as {@code 0xAARRGGBB}, opaque if the picker has no alpha */
    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = this.alpha ? color : ARGB.opaque(color);

        int r = ARGB.red(color), g = ARGB.green(color), b = ARGB.blue(color);
        int max = Math.max(r, Math.max(g, b)), min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        this.brightness = max / 255f;
        this.saturation = max == 0 ? 0 : delta / max;
        // grays have no hue, the previous one is kept so the field doesn't jump to red
        if (delta > 0) {
            float h = max == r ? (g - b) / delta : max == g ? (b - r) / delta + 2 : (r - g) / delta + 4;
            h /= 6;
            this.hue = h < 0 ? h + 1 : h;
        }

        this.setMessage(Component.literal(this.getHex()));
    }

    /** Called with the new color whenever the user changes it in the picker. */
    public void setResponder(IntConsumer responder) {
        this.responder = responder;
    }

    public String getHex() {
        return this.alpha
                ? String.format(Locale.ROOT, "#%08X", this.color)
                : String.format(Locale.ROOT, "#%06X", this.color & 0xFFFFFF);
    }

    private void updateFromHsv(int alphaValue) {
        this.color = Mth.hsvToArgb(Math.min(this.hue, 0.9999f), this.saturation, this.brightness, alphaValue);
        this.setMessage(Component.literal(this.getHex()));
        if (this.responder != null) this.responder.accept(this.color);
    }

    // --------------------------------------------------------------------------------------------
    // Layout
    // --------------------------------------------------------------------------------------------

    private int popupWidth() {
        return 2 * PADDING + FIELD_WIDTH;
    }

    private int popupHeight() {
        return PADDING + FIELD_HEIGHT + GAP + BAR_HEIGHT + (this.alpha ? GAP + BAR_HEIGHT : 0) + GAP + PREVIEW_HEIGHT + PADDING;
    }

    private int popupX() {
        int screenWidth = this.client.screen == null ? Integer.MAX_VALUE : this.client.screen.width;
        return Math.clamp(this.getRight() - this.popupWidth(), SCREEN_MARGIN, Math.max(SCREEN_MARGIN, screenWidth - this.popupWidth() - SCREEN_MARGIN));
    }

    /** Opens upwards if there isn't enough room below, computed from the current position because lists can move the widget. */
    private int popupY() {
        int screenHeight = this.client.screen == null ? Integer.MAX_VALUE : this.client.screen.height;
        boolean upwards = this.getBottom() + this.popupHeight() + SCREEN_MARGIN > screenHeight && this.getY() - this.popupHeight() > SCREEN_MARGIN;
        return upwards ? this.getY() - this.popupHeight() - 1 : this.getBottom() + 1;
    }

    private int fieldX() {
        return this.popupX() + PADDING;
    }

    private int fieldY() {
        return this.popupY() + PADDING;
    }

    private int hueY() {
        return this.fieldY() + FIELD_HEIGHT + GAP;
    }

    private int alphaY() {
        return this.hueY() + BAR_HEIGHT + GAP;
    }

    private int previewY() {
        return (this.alpha ? this.alphaY() : this.hueY()) + BAR_HEIGHT + GAP;
    }

    private boolean isOverPopup(double mouseX, double mouseY) {
        return this.open && UiUtil.inBound(this.popupX(), this.popupY(), this.popupX() + this.popupWidth(), this.popupY() + this.popupHeight(), mouseX, mouseY);
    }

    private DragTarget targetAt(double mouseX, double mouseY) {
        int left = this.fieldX(), right = left + FIELD_WIDTH;
        if (UiUtil.inBound(left, this.fieldY(), right, this.fieldY() + FIELD_HEIGHT, mouseX, mouseY)) return DragTarget.FIELD;
        if (UiUtil.inBound(left, this.hueY(), right, this.hueY() + BAR_HEIGHT, mouseX, mouseY)) return DragTarget.HUE;
        if (this.alpha && UiUtil.inBound(left, this.alphaY(), right, this.alphaY() + BAR_HEIGHT, mouseX, mouseY)) return DragTarget.ALPHA;
        return DragTarget.NONE;
    }

    // --------------------------------------------------------------------------------------------
    // Input
    // --------------------------------------------------------------------------------------------

    @Override
    public void onPress(InputWithModifiers input) {
        if (this.open) {
            this.close();
            return;
        }

        this.open = true;
        this.colorOnOpen = this.color;
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    @Override
    public void close() {
        this.open = false;
        this.dragTarget = DragTarget.NONE;
    }

    @Override
    public boolean isMouseOverPopup(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || this.isOverPopup(mouseX, mouseY);
    }

    @Override
    public void popupClicked(MouseButtonEvent event) {
        if (super.isMouseOver(event.x(), event.y())) {
            this.playDownSound(this.client.getSoundManager());
            this.close();
            return;
        }

        this.dragTarget = this.targetAt(event.x(), event.y());
        this.applyDrag(event.x(), event.y());
    }

    @Override
    public void popupDragged(MouseButtonEvent event) {
        this.applyDrag(event.x(), event.y());
    }

    @Override
    public void popupReleased(MouseButtonEvent event) {
        this.dragTarget = DragTarget.NONE;
    }

    private void applyDrag(double mouseX, double mouseY) {
        float relX = Mth.clamp((float) (mouseX - this.fieldX()) / (FIELD_WIDTH - 1), 0, 1);

        switch (this.dragTarget) {
            case FIELD -> {
                this.saturation = relX;
                this.brightness = 1 - Mth.clamp((float) (mouseY - this.fieldY()) / (FIELD_HEIGHT - 1), 0, 1);
                this.updateFromHsv(ARGB.alpha(this.color));
            }
            case HUE -> {
                this.hue = relX;
                this.updateFromHsv(ARGB.alpha(this.color));
            }
            case ALPHA -> this.updateFromHsv(Math.round(relX * 255));
            case NONE -> {}
        }
    }

    // --------------------------------------------------------------------------------------------
    // Rendering
    // --------------------------------------------------------------------------------------------

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int left = this.getX(), top = this.getY(), right = this.getRight(), bottom = this.getBottom();
        Themes.get().control(graphics, left, top, right, bottom, this.isHovered() || this.open, this.isFocused(), this.active);

        if (this.alpha) Themes.get().checkerboard(graphics, left + 3, top + 3, right - 3, bottom - 3);
        graphics.fill(left + 3, top + 3, right - 3, bottom - 3, this.color);
    }

    @Override
    public void extractPopup(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int popupLeft = this.popupX(), popupTop = this.popupY();
        int popupRight = popupLeft + this.popupWidth(), popupBottom = popupTop + this.popupHeight();

        graphics.fill(popupLeft + 2, popupTop + 2, popupRight + 2, popupBottom + 2, 0x60000000);
        Themes.get().popup(graphics, popupLeft, popupTop, popupRight, popupBottom);

        int left = this.fieldX(), right = left + FIELD_WIDTH;
        float hue = Math.min(this.hue, 0.9999f);

        // saturation from left to right, brightness from top to bottom
        int fy = this.fieldY();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            int topColor = Mth.hsvToArgb(hue, i / (float) (FIELD_WIDTH - 1), 1, 255);
            graphics.fillGradient(left + i, fy, left + i + 1, fy + FIELD_HEIGHT, topColor, 0xFF000000);
        }
        graphics.outline(left - 1, fy - 1, FIELD_WIDTH + 2, FIELD_HEIGHT + 2, 0xFF000000);
        int markerX = left + Math.round(this.saturation * (FIELD_WIDTH - 1));
        int markerY = fy + Math.round((1 - this.brightness) * (FIELD_HEIGHT - 1));
        graphics.outline(markerX - 3, markerY - 3, 7, 7, 0xFF000000);
        graphics.outline(markerX - 2, markerY - 2, 5, 5, 0xFFFFFFFF);

        // hue bar
        int hy = this.hueY();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            graphics.fill(left + i, hy, left + i + 1, hy + BAR_HEIGHT, Mth.hsvToArgb(Math.min(i / (float) FIELD_WIDTH, 0.9999f), 1, 1, 255));
        }
        this.extractBarMarker(graphics, left + Math.round(hue * (FIELD_WIDTH - 1)), hy);

        // alpha bar
        if (this.alpha) {
            int ay = this.alphaY();
            Themes.get().checkerboard(graphics, left, ay, right, ay + BAR_HEIGHT);
            int rgb = this.color & 0xFFFFFF;
            for (int i = 0; i < FIELD_WIDTH; i++) {
                int alpha = Math.round(i / (float) (FIELD_WIDTH - 1) * 255);
                graphics.fill(left + i, ay, left + i + 1, ay + BAR_HEIGHT, alpha << 24 | rgb);
            }
            this.extractBarMarker(graphics, left + Math.round(ARGB.alpha(this.color) / 255f * (FIELD_WIDTH - 1)), ay);
        }

        // old and new color next to the hex value
        int vy = this.previewY();
        int half = 20;
        if (this.alpha) Themes.get().checkerboard(graphics, left, vy, left + 2 * half, vy + PREVIEW_HEIGHT);
        graphics.fill(left, vy, left + half, vy + PREVIEW_HEIGHT, this.colorOnOpen);
        graphics.fill(left + half, vy, left + 2 * half, vy + PREVIEW_HEIGHT, this.color);
        graphics.outline(left - 1, vy - 1, 2 * half + 2, PREVIEW_HEIGHT + 2, 0xFF000000);
        graphics.text(this.client.font, this.getHex(), left + 2 * half + 6, UiUtil.centerY(vy, vy + PREVIEW_HEIGHT, 8), Themes.get().text);

        if (this.dragTarget != DragTarget.NONE || this.targetAt(mouseX, mouseY) != DragTarget.NONE) {
            graphics.requestCursor(CursorTypes.CROSSHAIR);
        } else if (super.isMouseOver(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }
    }

    private void extractBarMarker(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.outline(x - 2, y - 2, 5, BAR_HEIGHT + 4, 0xFF000000);
        graphics.outline(x - 1, y - 1, 3, BAR_HEIGHT + 2, 0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
