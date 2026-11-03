package net.morthen.resourceconfigapi.client.screen.entry;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.morthen.resourceconfigapi.api.ConfigOption;
import net.morthen.resourceconfigapi.client.ui.widget.ThemedEditBox;

import java.util.function.Function;

/** Edit box entry for strings and numbers, the text is converted with the parser. */
public class TextValueEntry<T> extends ValueListEntry<T> {
    private final ThemedEditBox editBox;
    private final Function<String, T> parser;

    /**
     * @param parser converts the text into the option type, throws a {@link RuntimeException} for invalid input
     * @param numeric true to only accept input that is a valid number or the start of one
     */
    public TextValueEntry(ConfigOption<?> option, Function<String, T> parser, int width, boolean numeric) {
        super(option);
        this.parser = parser;

        this.editBox = new ThemedEditBox(this.font, width, 18, Component.empty());
        this.editBox.setMaxLength(numeric ? 32 : 999);
        if (numeric) this.editBox.setFilter(text -> text.isEmpty() || text.equals("-") || this.parse(text) != null);
        this.editBox.setResponder(_ -> this.editBox.setError(!this.isValid()));
        this.editBox.setValue(String.valueOf(this.option.get()));
        this.addChild(this.editBox);
    }

    private T parse(String text) {
        try {
            return this.parser.apply(text);
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public boolean isValid() {
        return this.parse(this.editBox.getValue()) != null;
    }

    @Override
    protected AbstractWidget getControl() {
        return this.editBox;
    }

    @Override
    protected T getWidgetValue() {
        return this.parse(this.editBox.getValue());
    }

    @Override
    protected void setWidgetValue(T value) {
        this.editBox.setValue(String.valueOf(value));
    }
}
