package net.morthen.resourceconfigapi.client.screen.entry;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.morthen.resourceconfigapi.api.ConfigOption;
import net.morthen.resourceconfigapi.api.RestartRequirement;
import net.morthen.resourceconfigapi.client.ui.Themes;
import net.morthen.resourceconfigapi.client.ui.UiUtil;
import net.morthen.resourceconfigapi.client.ui.widget.ThemedIconButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Value list entry of a single option. Renders the entry as a card:
 * <pre>
 * | [icon] Label ...                    [control] [undo] |
 * </pre>
 * The icon is only shown for options that need a restart, a changed value turns the label
 * italic in the changed color. Subclasses provide the control and how its value is read and written,
 * the change tracking, saving, undo and reset are handled here.
 * @param <T> the boxed type of the option
 */
public abstract class ValueListEntry<T> extends ValueListRow {
    public static final int HEIGHT = 26;
    private static final int PADDING = 6;
    private static final int ICON_SIZE = 11;
    private static final int GAP = 4;
    private static final Component NOT_EDITABLE = Component.translatable("config.resourceconfigapi.screen.not_editable");
    private static final Component UNDO = Component.translatable("config.resourceconfigapi.screen.undo");
    private static final ResourceLocation UNDO_SPRITE = new ResourceLocation("resourceconfigapi", "textures/gui/sprites/widget/undo.png");
    private static final ResourceLocation UNDO_HIGHLIGHTED_SPRITE = new ResourceLocation("resourceconfigapi", "textures/gui/sprites/widget/undo_highlighted.png");

    protected final ConfigOption<T> option;
    private final Component label;
    private final Component tooltip;
    private final ResourceLocation indicatorSprite;
    private final Component indicatorText;
    private final ThemedIconButton undoButton;

    // horizontal bounds of the last rendered label, used for the tooltip hit test
    private int labelX;
    private int labelRight;
    private boolean labelTrimmed;
    private boolean editable = true;

    @SuppressWarnings("unchecked")
    protected ValueListEntry(ConfigOption<?> option) {
        // the factory only creates entries whose widget type matches the option type
        this.option = (ConfigOption<T>) option;
        this.label = Component.translatable(option.labelKey());
        this.tooltip = Component.translatable(option.tooltipKey());

        // only options that need a restart get an icon, the normal case stays unmarked
        RestartRequirement restart = option.restartRequirement();
        this.indicatorSprite = switch (restart) {
            case GAME -> new ResourceLocation("resourceconfigapi", "textures/gui/sprites/icon/game_restart.png");
            case WORLD -> new ResourceLocation("resourceconfigapi", "textures/gui/sprites/icon/world_restart.png");
            case NONE -> null;
        };
        this.indicatorText = switch (restart) {
            case GAME -> Component.translatable("config.resourceconfigapi.restart.game").withStyle(ChatFormatting.RED);
            case WORLD -> Component.translatable("config.resourceconfigapi.restart.world").withStyle(ChatFormatting.YELLOW);
            case NONE -> null;
        };

        this.undoButton = new ThemedIconButton(18, UNDO_SPRITE, UNDO_HIGHLIGHTED_SPRITE, 32, 16, UNDO, button -> this.undo());
        this.undoButton.setTooltip(Themes.get().tooltip(UNDO));
        this.addChild(this.undoButton);
    }

    /** @return the widget used to edit the value, or null if the value can't be edited in the ui */
    protected abstract AbstractWidget getControl();

    /** @return the widgets used to edit the value from left to right, by default only {@link #getControl()} */
    protected List<AbstractWidget> getControls() {
        AbstractWidget control = this.getControl();
        return control == null ? List.of() : List.of(control);
    }

    /** @return the value shown in the widget, null if the input is invalid */
    protected abstract T getWidgetValue();

    protected abstract void setWidgetValue(T value);

    @Override
    public boolean isChanged() {
        return !this.isValid() || !Objects.equals(this.getWidgetValue(), this.option.get());
    }

    @Override
    public void save() {
        if (this.isValid()) this.option.set(this.getWidgetValue());
    }

    @Override
    public void undo() {
        this.setWidgetValue(this.option.get());
    }

    @Override
    public void reset() {
        this.setWidgetValue(this.option.defaultValue());
    }

    @Override
    public int getRowHeight() {
        return HEIGHT;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    protected boolean isEditable() {
        return this.editable;
    }

    public String getOptionKey() {
        return this.option.key();
    }

    @Override
    public String getSearchText() {
        return this.label.getString() + " " + this.option.key();
    }

    @Override
    public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
        int cardLeft = this.getX(), cardTop = this.getY() + 1;
        int cardRight = this.getX() + this.getWidth(), cardBottom = this.getY() + this.getHeight() - 1;
        boolean changed = this.isChanged();

        Themes.get().fillRounded(graphics, cardLeft, cardTop, cardRight, cardBottom, hovered ? Themes.get().cardHovered : Themes.get().card);

        // the undo slot is always reserved, so controls don't jump around when a value changes
        int right = cardRight - PADDING;
        this.undoButton.visible = changed && this.editable;
        this.undoButton.setPosition(right - this.undoButton.getWidth(), UiUtil.centerY(cardTop, cardBottom, this.undoButton.getHeight()));
        this.undoButton.render(graphics, mouseX, mouseY, partialTick);
        right -= this.undoButton.getWidth() + GAP;

        List<AbstractWidget> controls = this.getControls();
        for (AbstractWidget control : Lists.reverse(controls)) {
            control.active = this.editable;
            control.setPosition(right - control.getWidth(), UiUtil.centerY(cardTop, cardBottom, control.getHeight()));
            control.render(graphics, mouseX, mouseY, partialTick);
            right = control.getX() - GAP;
        }
        if (!controls.isEmpty()) {
            right -= GAP;
        } else {
            int width = this.font.width(NOT_EDITABLE);
            graphics.drawString(this.font, NOT_EDITABLE, right - width, UiUtil.centerY(cardTop, cardBottom, 8), Themes.get().textMuted, false);
            right -= width + 2 * GAP;
        }

        // the icon slot is always reserved, so the labels of all rows stay aligned
        int iconX = cardLeft + PADDING;
        if (this.indicatorSprite != null) {
            UiUtil.blitTexture(graphics, this.indicatorSprite, iconX, UiUtil.centerY(cardTop, cardBottom, ICON_SIZE), ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }

        Component label = changed ? this.label.copy().withStyle(ChatFormatting.ITALIC) : this.label;
        int labelColor = changed ? Themes.get().changed : hovered ? Themes.get().textHighlighted : Themes.get().text;

        this.labelX = iconX + ICON_SIZE + PADDING;
        FormattedCharSequence text = UiUtil.ellipsize(this.font, label, right - this.labelX);
        this.labelRight = this.labelX + this.font.width(text);
        this.labelTrimmed = this.font.width(label) > right - this.labelX;
        graphics.drawString(this.font, text, this.labelX, UiUtil.centerY(cardTop, cardBottom, 8), labelColor);
    }

    @Override
    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        int cardTop = this.getY() + 1, cardBottom = this.getY() + this.getHeight() - 1;
        int iconX = this.getX() + PADDING;

        if (this.indicatorText != null && UiUtil.inBound(iconX - 2, cardTop, iconX + ICON_SIZE + 2, cardBottom, mouseX, mouseY)) {
            Themes.get().tooltip(graphics, this.font, mouseX, mouseY, List.of(this.indicatorText));
            return;
        }

        if (!UiUtil.inBound(this.labelX - 2, cardTop, this.labelRight + 2, cardBottom, mouseX, mouseY)) return;

        List<Component> paragraphs = new ArrayList<>();
        if (this.labelTrimmed) paragraphs.add(this.label);
        if (ComponentUtils.isTranslationResolvable(this.tooltip)) paragraphs.add(this.tooltip);
        Themes.get().tooltip(graphics, this.font, mouseX, mouseY, paragraphs);
    }

    /** Entry for options whose type can't be edited in the ui, the value is only shown in the file. */
    static final class Unsupported extends ValueListEntry<Object> {
        Unsupported(ConfigOption<?> option) {
            super(option);
        }

        @Override
        protected AbstractWidget getControl() {
            return null;
        }

        @Override
        protected Object getWidgetValue() {
            return this.option.get();
        }

        @Override
        protected void setWidgetValue(Object value) {}
    }
}
