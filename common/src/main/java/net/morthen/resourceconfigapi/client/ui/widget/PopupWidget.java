package net.morthen.resourceconfigapi.client.ui.widget;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Widget with a popup that overlaps other widgets, like the menu of a {@link ThemedDropdown}.
 * While the popup is open, {@link PopupLayer} routes the input to it and draws it on top.
 */
public interface PopupWidget {

    boolean isOpen();

    void close();

    /** @return true if the position is on the widget itself or on the open popup */
    boolean isMouseOverPopup(double mouseX, double mouseY);

    /** A click on the widget or the popup while it is open. */
    void popupClicked(double mouseX, double mouseY, int button);

    /** The mouse is dragged while the popup is open, also when it left the popup. */
    default void popupDragged(double mouseX, double mouseY, int button) {}

    default void popupReleased(double mouseX, double mouseY, int button) {}

    /** @return true if the scroll was used */
    default boolean popupScrolled(double mouseX, double mouseY, double scrollY) {
        return false;
    }

    void renderPopup(GuiGraphics graphics, int mouseX, int mouseY);
}
