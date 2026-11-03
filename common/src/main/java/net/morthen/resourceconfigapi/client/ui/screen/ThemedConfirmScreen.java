package net.morthen.resourceconfigapi.client.ui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.morthen.resourceconfigapi.client.ui.Themes;
import net.morthen.resourceconfigapi.client.ui.widget.ThemedButton;

import java.util.List;

/**
 * Yes/No dialog drawn as a panel in the middle of the screen. Escape counts as "no".
 * <pre>
 * minecraft.setScreen(new ThemedConfirmScreen(confirmed -> { ... }, title, message)
 *         .confirmButton(Component.literal("Delete"), ThemedButton.Style.DANGER));
 * </pre>
 */
public class ThemedConfirmScreen extends Screen {
    private static final int PADDING = 12;
    private static final int MAX_WIDTH = 300;

    private final BooleanConsumer callback;
    private final Component message;

    private Component confirmLabel = CommonComponents.GUI_YES;
    private Component cancelLabel = CommonComponents.GUI_NO;
    private ThemedButton.Style confirmStyle = ThemedButton.Style.PRIMARY;

    private List<FormattedCharSequence> lines = List.of();
    private int panelX, panelY, panelWidth, panelHeight;

    public ThemedConfirmScreen(BooleanConsumer callback, Component title, Component message) {
        super(title);
        this.callback = callback;
        this.message = message;
    }

    public ThemedConfirmScreen confirmButton(Component label, ThemedButton.Style style) {
        this.confirmLabel = label;
        this.confirmStyle = style;
        return this;
    }

    public ThemedConfirmScreen cancelButton(Component label) {
        this.cancelLabel = label;
        return this;
    }

    @Override
    protected void init() {
        this.panelWidth = Math.min(MAX_WIDTH, this.width - 32);
        this.lines = this.font.split(this.message, this.panelWidth - 2 * PADDING);
        this.panelHeight = PADDING + this.font.lineHeight + 8 + this.lines.size() * this.font.lineHeight + PADDING + 20 + PADDING;
        this.panelX = (this.width - this.panelWidth) / 2;
        this.panelY = (this.height - this.panelHeight) / 2;

        int buttonWidth = (this.panelWidth - 2 * PADDING - 8) / 2;
        int buttonY = this.panelY + this.panelHeight - PADDING - 20;

        this.addRenderableWidget(ThemedButton.builder(this.cancelLabel, _ -> this.callback.accept(false))
                .bounds(this.panelX + PADDING, buttonY, buttonWidth, 20).build());
        this.addRenderableWidget(ThemedButton.builder(this.confirmLabel, _ -> this.callback.accept(true))
                .style(this.confirmStyle).bounds(this.panelX + this.panelWidth - PADDING - buttonWidth, buttonY, buttonWidth, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int left = this.panelX, top = this.panelY, right = left + this.panelWidth, bottom = top + this.panelHeight;
        Themes.get().popup(graphics, left, top, right, bottom);

        int y = top + PADDING;
        graphics.centeredText(this.font, this.title, this.width / 2, y, Themes.get().textHighlighted);
        y += this.font.lineHeight + 8;

        for (FormattedCharSequence line : this.lines) {
            graphics.centeredText(this.font, line, this.width / 2, y, Themes.get().textMuted);
            y += this.font.lineHeight;
        }

        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public void onClose() {
        this.callback.accept(false);
    }
}
