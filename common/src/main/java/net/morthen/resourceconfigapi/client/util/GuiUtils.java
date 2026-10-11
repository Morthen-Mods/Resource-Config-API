package net.morthen.resourceconfigapi.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.morthen.resourceconfigapi.ConfigConstants;
import net.morthen.resourceconfigapi.annotations.ConfigEntry;
import net.morthen.resourceconfigapi.client.ClientConstants;
import net.morthen.resourceconfigapi.client.gui.screen.ResourceConfigScreen;
import net.morthen.resourceconfigapi.client.gui.tooltip.ResourceConfigTextTooltip;
import net.morthen.resourceconfigapi.client.gui.tooltip.ResourceTooltipProvider;

import java.lang.reflect.Field;
import java.util.List;

public class GuiUtils {

    private static final Identifier directImpact = ConfigConstants.of("textures/gui/sprites/icon/direct_impact.png");
    private static final Identifier worldRestart = ConfigConstants.of("textures/gui/sprites/icon/world_restart.png");
    private static final Identifier gameRestart = ConfigConstants.of("textures/gui/sprites/icon/game_restart.png");

    public static Font getFont() {
        return Minecraft.getInstance().font;
    }

    public static void renderIcon(ResourceConfigScreen screen, GuiGraphicsExtractor guiGraphics, Field field, int xPos, int yPos, int mouseX, int mouseY) {
        ConfigEntry config = field.getAnnotation(ConfigEntry.class);

        if (!config.needsGameRestart() && !config.needsWorldRestart()) {
            renderIcon(screen, guiGraphics, directImpact, ClientConstants.DIRECT_IMPACT_TOOLTIP, xPos, yPos, mouseX, mouseY);
        }
        if (config.needsGameRestart()) {
            renderIcon(screen, guiGraphics, gameRestart, ClientConstants.NEEDS_GAME_RESTART_TOOLTIP, xPos, yPos, mouseX, mouseY);
        }
        if (config.needsWorldRestart()) {
            renderIcon(screen, guiGraphics, worldRestart, ClientConstants.NEEDS_WORLD_RESTART_TOOLTIP, xPos, yPos, mouseX, mouseY);
        }
    }

    public static void renderIcon(ResourceConfigScreen screen, GuiGraphicsExtractor guiGraphics, Identifier texture, Component tooltip, int xPos, int yPos, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, xPos, yPos, 0, 0, 11, 11, 11, 11);

        if (inBounds(xPos, yPos, xPos + 11, yPos + 11, mouseX, mouseY)) {
            screen.addTooltip(tooltip);
        }
    }

    public static void drawStringWithTooltip(ResourceConfigScreen screen, ResourceTooltipProvider provider, GuiGraphicsExtractor guiGraphics, Component title, Component tooltip, int xPos, int yPos, int mouseX, int mouseY) {
        if (title != null) {
            guiGraphics.text(getFont(), title, xPos, yPos, -1, false);

            if (tooltip != null) {
                int xMax = xPos + getFont().width(title.getString());
                int yMax = yPos + getFont().lineHeight;
                if (hasTranslation(tooltip) && inBounds(xPos, yPos, xMax, yMax, mouseX, mouseY)) {
                    screen.addTooltip(provider.getTooltip());
                }
            }
        }
    }

    public static ResourceConfigTextTooltip createTooltip(Component component) {
        List<FormattedCharSequence> sequence = getFont().split(component, 170);
        return new ResourceConfigTextTooltip(sequence);
    }

    public static boolean inBounds(int xPos, int yPos, int xMax , int yMax, int mouseX, int mouseY) {
        return xPos <= mouseX && xMax >= mouseX
                && yPos <= mouseY && yMax >= mouseY;
    }

    public static boolean hasTranslation(Component component) {
        String key = getComponentKey(component);
        String translated = ClientLanguage.getInstance().getOrDefault(key);

        return !translated.equals(key);
    }

    private static String getComponentKey(Component component) {
        String fullKey = component.toString();
        return fullKey.substring(17, fullKey.length() - 11);
    }
}
