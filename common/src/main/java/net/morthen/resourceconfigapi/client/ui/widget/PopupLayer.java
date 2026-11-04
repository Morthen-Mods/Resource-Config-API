package net.morthen.resourceconfigapi.client.ui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Routes input to an open {@link PopupWidget} and draws the popup on top of everything else.
 * The popup overlaps other widgets, so the screen has to ask the layer first:
 * <pre>
 * private final PopupLayer popups = new PopupLayer(() -> List.of(myDropdown, myColorPicker));
 *
 * public boolean mouseClicked(double x, double y, int button) {
 *     return popups.mouseClicked(x, y, button) || super.mouseClicked(x, y, button);
 * }
 * public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
 *     return popups.mouseDragged(x, y, button) || super.mouseDragged(x, y, button, dx, dy);
 * }
 * public boolean mouseReleased(double x, double y, int button) {
 *     return popups.mouseReleased(x, y, button) || super.mouseReleased(x, y, button);
 * }
 * public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
 *     return popups.keyPressed(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
 * }
 * public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
 *     return popups.mouseScrolled(x, y, scrollY) || super.mouseScrolled(x, y, scrollX, scrollY);
 * }
 * public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
 *     // hides the mouse from the widgets below an open popup
 *     super.render(graphics, popups.isOpen() ? -1 : mouseX, popups.isOpen() ? -1 : mouseY, partialTick);
 *     popups.render(graphics, mouseX, mouseY);
 * }
 * </pre>
 */
public class PopupLayer {
    /** Z offset of the popup, above the widgets but below tooltips. */
    private static final float POPUP_Z = 200;

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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        PopupWidget popup = this.getOpen();
        if (popup == null) return false;

        if (popup.isMouseOverPopup(mouseX, mouseY)) popup.popupClicked(mouseX, mouseY, button);
        else popup.close();
        return true;
    }

    /** @return true if the drag was consumed */
    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        PopupWidget popup = this.getOpen();
        if (popup == null) return false;

        popup.popupDragged(mouseX, mouseY, button);
        return true;
    }

    /** @return true if the release was consumed */
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        PopupWidget popup = this.getOpen();
        if (popup == null) return false;

        popup.popupReleased(mouseX, mouseY, button);
        return true;
    }

    /** Closes the open popup on escape. @return true if the key was consumed */
    public boolean keyPressed(int keyCode) {
        PopupWidget popup = this.getOpen();
        if (popup == null || keyCode != InputConstants.KEY_ESCAPE) return false;

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

    /** Draws the open popup above everything else, call it after everything else was drawn. */
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        PopupWidget popup = this.getOpen();
        if (popup == null) return;

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, POPUP_Z);
        popup.renderPopup(graphics, mouseX, mouseY);
        graphics.pose().popPose();
    }
}
