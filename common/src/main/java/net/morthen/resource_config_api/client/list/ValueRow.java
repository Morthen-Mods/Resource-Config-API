package net.morthen.resource_config_api.client.list;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.Identifier;
import net.morthen.resource_config_api.ConfigConstants;
import net.morthen.resource_config_api.api.ConfigEntry;
import net.morthen.resource_config_api.api.ModConfig;
import net.morthen.resource_config_api.client.widget.ValueInput;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ValueRow extends Row {
    private static final WidgetSprites RESET_SPRITES = new WidgetSprites(
            ConfigConstants.of("widget/undo"), ConfigConstants.of("widget/undo"), ConfigConstants.of("widget/undo_highlighted"));

    private final ModConfig config;
    private final String key;
    private final boolean editable;
    private final ValueInput input;
    private final ImageButton resetButton;

    private final Component label;
    private final Component tooltip;
    private final Identifier restartIcon;
    private final Component restartTooltip;

    public ValueRow(ModConfig config, String key, boolean editable) {
        this.config = config;
        this.key = key;
        this.editable = editable;

        input = ValueInput.create(config, key);
        input.write().accept(config.get(key));
        input.widget().active = editable;
        if (input.widget() instanceof EditBox editBox) editBox.setEditable(editable);

        Component resetText = ConfigConstants.RESET;
        resetButton = new ImageButton(16, 16, RESET_SPRITES, button -> input.write().accept(config.getDefault(key)), resetText);
        resetButton.setTooltip(Tooltip.create(resetText));

        String translationKey = config.translationKey(key);
        label = Component.translatableWithFallback(translationKey + ".label", key);
        tooltip = Component.translatable(translationKey + ".tooltip");

        ConfigEntry entry = config.entry(key);
        restartIcon = entry.impact().getIcon();
        restartTooltip = entry.impact().getTooltip();
    }

    public AbstractWidget getWidget() {
        return input.widget();
    }

    // null while the widget holds invalid input
    public Object getValue() {
        return config.parse(key, input.read().get());
    }

    public void collectChange(Map<String, Object> changes) {
        Object value = getValue();
        if (value != null && !Objects.equals(value, config.get(key))) changes.put(key, value);
    }

    @Override
    public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        if (hovered) graphics.fill(getX(), getY(), getContentRight(), getContentBottom(), 0x18FFFFFF);

        int x = getContentX();
        int middle = getContentYMiddle();
        int labelY = middle - font.lineHeight / 2;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, restartIcon, x, middle - 6, 11, 11);
        graphics.text(font, label, x + 15, labelY, -1);

        if (isInside(mouseX, mouseY, x, middle - 6, 11, 11)) {
            graphics.setTooltipForNextFrame(font, restartTooltip, mouseX, mouseY);
        } else if (isInside(mouseX, mouseY, x + 15, labelY, font.width(label), font.lineHeight)
                && ComponentUtils.isTranslationResolvable(tooltip)) {
            graphics.setTooltipForNextFrame(font, font.split(tooltip, 200), mouseX, mouseY);
        }

        Object value = getValue();
        AbstractWidget widget = input.widget();
        if (widget instanceof EditBox editBox) {
            editBox.setTextColor(value == null ? 0xFFFF5555 : EditBox.DEFAULT_TEXT_COLOR);
        }
        widget.setPosition(getContentRight() - widget.getWidth() - 20, middle - (widget.getHeight() / 2) - 1);
        widget.extractRenderState(graphics, mouseX, mouseY, a);

        resetButton.visible = editable && !Objects.equals(value, config.getDefault(key));
        resetButton.setPosition(getContentRight() - resetButton.getWidth(), middle - resetButton.getHeight() / 2);
        resetButton.extractRenderState(graphics, mouseX, mouseY, a);
    }

    private static boolean isInside(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of(input.widget(), resetButton);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(input.widget(), resetButton);
    }
}
