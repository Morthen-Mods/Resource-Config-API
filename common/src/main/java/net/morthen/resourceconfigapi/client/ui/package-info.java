/**
 * Reusable widgets that draw themselves with the selected {@link net.morthen.resourceconfigapi.client.ui.Theme},
 * by default in the vanilla look.
 * The package only depends on vanilla classes, it can be copied into other projects as a whole
 * (only the package name has to be adjusted).
 *
 * <ul>
 *     <li>{@link net.morthen.resourceconfigapi.client.ui.Theme} / {@link net.morthen.resourceconfigapi.client.ui.Themes} - colors and drawing helpers used by every widget, switchable and extendable</li>
 *     <li>{@link net.morthen.resourceconfigapi.client.ui.widget.ThemedButton} - button with default, primary and danger style</li>
 *     <li>{@link net.morthen.resourceconfigapi.client.ui.widget.ThemedIconButton} - sprite only button</li>
 *     <li>{@link net.morthen.resourceconfigapi.client.ui.widget.ThemedEditBox} - edit box with error state and input filter</li>
 *     <li>{@link net.morthen.resourceconfigapi.client.ui.widget.ThemedSlider} - slider over a real value range with snapping</li>
 *     <li>{@link net.morthen.resourceconfigapi.client.ui.widget.ThemedToggle} - animated on/off switch</li>
 *     <li>{@link net.morthen.resourceconfigapi.client.ui.widget.ThemedDropdown} - dropdown</li>
 *     <li>{@link net.morthen.resourceconfigapi.client.ui.widget.ThemedColorPicker} - color swatch with hsv picker and optional alpha</li>
 *     <li>{@link net.morthen.resourceconfigapi.client.ui.widget.PopupWidget} - widgets with a popup like the dropdown menu and the color picker, need a
 *         {@link net.morthen.resourceconfigapi.client.ui.widget.PopupLayer} in the screen</li>
 *     <li>{@link net.morthen.resourceconfigapi.client.ui.widget.ThemedTabBar} - replacement for the vanilla tab navigation bar</li>
 *     <li>{@link net.morthen.resourceconfigapi.client.ui.list.ThemedObjectList} /
 *         {@link net.morthen.resourceconfigapi.client.ui.list.ThemedContainerList} - list base classes</li>
 *     <li>{@link net.morthen.resourceconfigapi.client.ui.screen.ThemedConfirmScreen} - yes/no dialog</li>
 *     <li>{@link net.morthen.resourceconfigapi.client.ui.WrappedTooltip} - tooltip paragraph, see {@code Themes.get().tooltip(...)}</li>
 * </ul>
 */
package net.morthen.resourceconfigapi.client.ui;
