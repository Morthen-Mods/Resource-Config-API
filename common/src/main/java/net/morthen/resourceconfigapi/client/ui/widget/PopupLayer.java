package net.morthen.resourceconfigapi.client.ui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Routes input to an open {@link PopupWidget} and draws the popup on top of everything else.
 * The popup overlaps other widgets, so the screen has to ask the layer first:
 * <pre>
 * private final PopupLayer popups = new PopupLayer(() -> List.of(myDropdown, myColorPicker));
 *
 * public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
 *     return popups.mouseClicked(event) || super.mouseClicked(event, doubleClick);
 * }
 * public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
 *     return popups.mouseDragged(event) || super.mouseDragged(event, dx, dy);
 * }
 * public boolean mouseReleased(MouseButtonEvent event) {
 *     return popups.mouseReleased(event) || super.mouseReleased(event);
 * }
 * public boolean keyPressed(KeyEvent event) {
 *     return popups.keyPressed(event) || super.keyPressed(event);
 * }
 * public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
 *     return popups.mouseScrolled(x, y, scrollY) || super.mouseScrolled(x, y, scrollX, scrollY);
 * }
 * public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
 *     // hides the mouse from the widgets below an open popup
 *     super.extractRenderState(graphics, popups.isOpen() ? -1 : mouseX, popups.isOpen() ? -1 : mouseY, a);
 *     popups.extract(graphics, mouseX, mouseY);
 * }
 * </pre>
 */
public class PopupLayer {
    private final Supplier<? extends Collection<? extends PopupWidget>> popups;

    /** @param popups supplies the popup widgets that are currently visible on the screen */
    public PopupLayer(Supplier<? extends Collection<? extends PopupWidget>> popups) {
        this.popups = popups;
    }

    /** @return the open popup or null */
    public PopupWidget getOpen() {
        for (PopupWidget popup : this.popups.get()) {
            if (popup.isOpen()) return popup;
        }
        return null;
    }

    public boolean isOpen() {
        return this.getOpen() != null;
    }

    public void closeAll() {
        this.popups.get().forEach(PopupWidget::close);
    }

    /**
     * While a popup is open every click is consumed, a click outside of it only closes it.
     * @return true if the click was consumed
     */
    public boolean mouseClicked(MouseButtonEvent event) {
        PopupWidget popup = this.getOpen();
        if (popup == null) return false;

        if (popup.isMouseOverPopup(event.x(), event.y())) popup.popupClicked(event);
        else popup.close();
        return true;
    }

    /** @return true if the drag was consumed */
    public boolean mouseDragged(MouseButtonEvent event) {
        PopupWidget popup = this.getOpen();
        if (popup == null) return false;

        popup.popupDragged(event);
        return true;
    }

    /** @return true if the release was consumed */
    public boolean mouseReleased(MouseButtonEvent event) {
        PopupWidget popup = this.getOpen();
        if (popup == null) return false;

        popup.popupReleased(event);
        return true;
    }

    /** Closes the open popup on escape. @return true if the key was consumed */
    public boolean keyPressed(KeyEvent event) {
        PopupWidget popup = this.getOpen();
        if (popup == null || event.key() != InputConstants.KEY_ESCAPE) return false;

        popup.close();
        return true;
    }

    /** Scrolling over the popup is passed to it, scrolling anywhere else closes it. @return true if the scroll was consumed */
    public boolean mouseScrolled(double x, double y, double scrollY) {
        PopupWidget popup = this.getOpen();
        if (popup == null) return false;

        if (popup.isMouseOverPopup(x, y)) return popup.popupScrolled(x, y, scrollY);
        popup.close();
        return false;
    }

    /** Draws the open popup on a new stratum, call it after everything else was drawn. */
    public void extract(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        PopupWidget popup = this.getOpen();
        if (popup == null) return;

        graphics.nextStratum();
        popup.extractPopup(graphics, mouseX, mouseY);
    }
}
