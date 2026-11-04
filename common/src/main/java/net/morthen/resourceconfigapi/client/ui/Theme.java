package net.morthen.resourceconfigapi.client.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.morthen.resourceconfigapi.client.ui.widget.ThemedButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Colors and drawing primitives shared by all widgets. The defaults of this class are the vanilla look:
 * square corners, gray beveled buttons and white text. The modern look (rounded corners, flat translucent
 * panels, a gold accent and a green primary color) is the theme {@code modern.json}.
 * <p>
 * Colors and the corner radius of other themes can be set in {@code assets/resourceconfigapi/themes/<id>.json}
 * by a resource pack, see {@code ThemeLoader}. For a design with different drawing extend this class,
 * override the drawing methods you want to look different, register it with
 * {@link Themes#register(String, Theme)} and activate it with {@link Themes#select(String)}.
 * <p>
 * The whole {@code client.ui} package only depends on vanilla classes, so it can be copied into
 * other projects as is.
 */
public class Theme {
    /** Number of corner pixels that are cut off per corner, 0 for square corners. */
    public int cornerRadius = 0;

    // Panels / Cards
    public int panel = 0xA0000000;
    public int panelBorder = 0xFF000000;
    public int card = 0x40000000;
    public int cardHovered = 0x30FFFFFF;
    public int selected = 0x40FFFFFF;
    public int divider = 0x60FFFFFF;
    public int header = 0x66000000;

    // Popups (dropdown menu, color picker, dialogs)
    public int popup = 0xF2141414;
    public int popupBorder = 0xFF505050;

    // Input controls (edit box, dropdown, slider track)
    public int control = 0xFF000000;
    public int controlHovered = 0xFF000000;
    public int controlDisabled = 0x60101010;
    public int controlBorder = 0xFFA0A0A0;
    public int controlBorderHovered = 0xFFD0D0D0;
    public int controlBorderFocused = 0xFFFFFFFF;
    public int controlBorderDisabled = 0xFF2E2E2E;

    // Buttons
    public int button = 0xFF6F6F6F;
    public int buttonHovered = 0xFF7F8FBF;
    public int buttonDisabled = 0xFF3A3A3A;
    public int buttonPrimary = 0xFF4F7F4F;
    public int buttonPrimaryHovered = 0xFF6F9F6F;
    public int buttonDanger = 0xFF8F4F4F;
    public int buttonDangerHovered = 0xFFAF6F6F;
    public int buttonBorder = 0xFF000000;
    public int buttonHighlight = 0x60FFFFFF;
    public int buttonShadow = 0x60000000;

    // Accents
    public int accent = 0xFFFFFF55;
    public int changed = 0xFFFFD84A;
    public int invalid = 0xFFFF5555;
    public int toggleOn = 0xFF55AA55;
    public int toggleOff = 0xFF555555;
    public int sliderFill = 0x8055AA55;
    public int knob = 0xFFC6C6C6;
    public int knobHovered = 0xFFE2E2E2;

    // Text
    public int text = 0xFFFFFFFF;
    public int textHighlighted = 0xFFFFFFFF;
    public int textMuted = 0xFFA0A0A0;
    public int textDisabled = 0xFF5F5F5F;

    // Scrollbar
    public int scrollbarTrack = 0x1AFFFFFF;
    public int scrollbarThumb = 0x80FFFFFF;
    public int scrollbarThumbHovered = 0xC0FFFFFF;

    /** Width at which the paragraphs of {@link #tooltip(GuiGraphics, Font, int, int, List)} are wrapped. */
    public static final int TOOLTIP_WIDTH = 170;

    // --------------------------------------------------------------------------------------------
    // Tooltips
    // --------------------------------------------------------------------------------------------

    /**
     * Widget tooltip of the theme, use it instead of {@link Tooltip#create(Component)}.
     * Minecraft 1.21.1 has no tooltip styles yet, so it looks like the vanilla one.
     */
    public Tooltip tooltip(Component message) {
        return Tooltip.create(message);
    }

    /** Draws a tooltip at the mouse position, every paragraph is wrapped on its own. */
    public void tooltip(GuiGraphics graphics, Font font, int mouseX, int mouseY, List<Component> paragraphs) {
        if (paragraphs.isEmpty()) return;

        List<FormattedCharSequence> lines = new ArrayList<>();
        for (Component paragraph : paragraphs) lines.addAll(font.split(paragraph, TOOLTIP_WIDTH));
        graphics.renderTooltip(font, lines, mouseX, mouseY);
    }

    // --------------------------------------------------------------------------------------------
    // Shapes
    // --------------------------------------------------------------------------------------------

    /** Horizontal inset of the given row (0 is the outermost) of a corner with this radius. */
    private static int cornerInset(int radius, int row) {
        double dy = radius - row;
        return (int) Math.ceil(radius - Math.sqrt(radius * radius - dy * dy));
    }

    private int radiusFor(int left, int top, int right, int bottom) {
        return Math.clamp(this.cornerRadius, 0, Math.max(0, Math.min(right - left, bottom - top) / 2));
    }

    /** Fills the rectangle, the corners are rounded according to {@link #cornerRadius}. */
    public void fillRounded(GuiGraphics graphics, int left, int top, int right, int bottom, int color) {
        this.fillRounded(graphics, left, top, right, bottom, color, this.radiusFor(left, top, right, bottom));
    }

    /**
     * Fills the inside of the rectangle that {@link #outlineRounded} or the border fill of
     * {@link #fillRounded} draws with the same bounds, the corners match the border exactly.
     */
    public void fillInner(GuiGraphics graphics, int left, int top, int right, int bottom, int color) {
        int radius = Math.max(0, this.radiusFor(left, top, right, bottom) - 1);
        this.fillRounded(graphics, left + 1, top + 1, right - 1, bottom - 1, color, radius);
    }

    /** Horizontal inset of the top and bottom row of {@link #fillInner}, for lines drawn along them. */
    public int innerInset(int left, int top, int right, int bottom) {
        return cornerInset(Math.max(0, this.radiusFor(left, top, right, bottom) - 1), 0);
    }

    private void fillRounded(GuiGraphics graphics, int left, int top, int right, int bottom, int color, int r) {
        r = Math.clamp(r, 0, Math.max(0, Math.min(right - left, bottom - top) / 2));
        for (int i = 0; i < r; i++) {
            int inset = cornerInset(r, i);
            graphics.fill(left + inset, top + i, right - inset, top + i + 1, color);
            graphics.fill(left + inset, bottom - i - 1, right - inset, bottom - i, color);
        }
        graphics.fill(left, top + r, right, bottom - r, color);
    }

    /** Outline matching {@link #fillRounded}. */
    public void outlineRounded(GuiGraphics graphics, int left, int top, int right, int bottom, int color) {
        int r = this.radiusFor(left, top, right, bottom);
        for (int i = 0; i < r; i++) {
            int from = cornerInset(r, i);
            // the top row is the whole edge, the following ones only the step towards the side, at least one pixel
            int to = i == 0 ? from : Math.max(cornerInset(r, i - 1), from + 1);
            if (i == 0) {
                graphics.fill(left + from, top, right - from, top + 1, color);
                graphics.fill(left + from, bottom - 1, right - from, bottom, color);
            } else {
                graphics.fill(left + from, top + i, left + to, top + i + 1, color);
                graphics.fill(right - to, top + i, right - from, top + i + 1, color);
                graphics.fill(left + from, bottom - i - 1, left + to, bottom - i, color);
                graphics.fill(right - to, bottom - i - 1, right - from, bottom - i, color);
            }
        }
        if (r == 0) {
            graphics.fill(left, top, right, top + 1, color);
            graphics.fill(left, bottom - 1, right, bottom, color);
        }
        graphics.fill(left, top + Math.max(r, 1), left + 1, bottom - Math.max(r, 1), color);
        graphics.fill(right - 1, top + Math.max(r, 1), right, bottom - Math.max(r, 1), color);
    }

    // --------------------------------------------------------------------------------------------
    // Elements
    // --------------------------------------------------------------------------------------------

    /** Background of panels like the sidebar and the value list. */
    public void panel(GuiGraphics graphics, int left, int top, int right, int bottom) {
        this.fillRounded(graphics, left, top, right, bottom, this.panel);
        this.outlineRounded(graphics, left, top, right, bottom, this.panelBorder);
    }

    /** Background of popups like the dropdown menu, the color picker and dialogs. */
    public void popup(GuiGraphics graphics, int left, int top, int right, int bottom) {
        this.fillRounded(graphics, left, top, right, bottom, this.popup);
        this.outlineRounded(graphics, left, top, right, bottom, this.popupBorder);
    }

    /** Background of input controls like edit boxes, dropdowns and slider tracks. */
    public void control(GuiGraphics graphics, int left, int top, int right, int bottom, boolean hovered, boolean focused, boolean active) {
        this.control(graphics, left, top, right, bottom, hovered, focused, active, 0);
    }

    /**
     * Background of input controls.
     * @param borderOverride border color that replaces the state based one, 0 for none
     */
    public void control(GuiGraphics graphics, int left, int top, int right, int bottom, boolean hovered, boolean focused, boolean active, int borderOverride) {
        int fill = !active ? this.controlDisabled : (hovered ? this.controlHovered : this.control);
        int border = !active ? this.controlBorderDisabled
                : focused ? this.controlBorderFocused
                : hovered ? this.controlBorderHovered
                : this.controlBorder;
        if (borderOverride != 0 && active) border = borderOverride;

        this.fillRounded(graphics, left, top, right, bottom, fill);
        this.outlineRounded(graphics, left, top, right, bottom, border);
    }

    /** Button background including the bevel. */
    public void button(GuiGraphics graphics, int left, int top, int right, int bottom, ThemedButton.Style style, boolean highlighted, boolean active) {
        int fill;
        if (!active) {
            fill = this.buttonDisabled;
        } else {
            fill = switch (style) {
                case DEFAULT -> highlighted ? this.buttonHovered : this.button;
                case PRIMARY -> highlighted ? this.buttonPrimaryHovered : this.buttonPrimary;
                case DANGER -> highlighted ? this.buttonDangerHovered : this.buttonDanger;
            };
        }
        int border = highlighted ? this.controlBorderFocused : this.buttonBorder;

        this.fillRounded(graphics, left, top, right, bottom, border);
        this.fillInner(graphics, left, top, right, bottom, fill);
        if (active) {
            // the bevel lines follow the corners of the fill
            int inset = this.innerInset(left, top, right, bottom);
            graphics.fill(left + 1 + inset, top + 1, right - 1 - inset, top + 2, this.buttonHighlight);
            graphics.fill(left + 1 + inset, bottom - 2, right - 1 - inset, bottom - 1, this.buttonShadow);
        }
    }

    /** Small light gray knob with the vanilla button bevel, used by toggles and sliders. */
    public void knob(GuiGraphics graphics, int left, int top, int right, int bottom, boolean hovered, boolean active) {
        int color = !active ? 0xFF7A7A7A : (hovered ? this.knobHovered : this.knob);
        this.fillRounded(graphics, left, top, right, bottom, 0xFF000000);
        this.fillInner(graphics, left, top, right, bottom, color);
        int inset = this.innerInset(left, top, right, bottom);
        graphics.fill(left + 1 + inset, top + 1, right - 1 - inset, top + 2, active ? 0xFFFFFFFF : 0xFF9A9A9A);
        graphics.fill(left + 1 + inset, bottom - 2, right - 1 - inset, bottom - 1, 0xFF6F6F6F);
    }

    /** Gray checkerboard with 4px cells, drawn behind transparent colors. */
    public void checkerboard(GuiGraphics graphics, int left, int top, int right, int bottom) {
        graphics.fill(left, top, right, bottom, 0xFFC0C0C0);
        for (int y = top; y < bottom; y += 4) {
            for (int x = left + ((y - top) / 4 % 2) * 4; x < right; x += 8) {
                graphics.fill(x, y, Math.min(x + 4, right), Math.min(y + 4, bottom), 0xFF808080);
            }
        }
    }

    /** Small chevron, 5px wide and 3px high. */
    public void chevron(GuiGraphics graphics, int x, int y, boolean up, int color) {
        for (int i = 0; i < 3; i++) {
            int row = up ? 2 - i : i;
            graphics.fill(x + i, y + row, x + 5 - i, y + row + 1, color);
        }
    }

    /** Slim scrollbar, drawn centered inside the given hit area. */
    public void scrollbar(GuiGraphics graphics, int x, int y, int width, int height, int thumbY, int thumbHeight, boolean hovered) {
        int barX = x + width / 2 - 1;
        graphics.fill(barX, y, barX + 2, y + height, this.scrollbarTrack);
        this.fillRounded(graphics, barX - (hovered ? 1 : 0), thumbY, barX + 2 + (hovered ? 1 : 0), thumbY + thumbHeight,
                hovered ? this.scrollbarThumbHovered : this.scrollbarThumb);
    }
}
