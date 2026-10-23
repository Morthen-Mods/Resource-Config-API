package net.morthen.resourceconfigapi.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.morthen.resourceconfigapi.ConfigConstants;

public enum RestartRequirement {
    NONE("direct_impact"),
    WORLD("world_restart"),
    GAME("game_restart");

    final Identifier icon;
    final Component tooltip;

    RestartRequirement(String key) {
        icon = ConfigConstants.of("icon/" + key);
        tooltip = Component.translatable("resource_config_api.base.restart_requirement." + key);
    }

    public Component getTooltip() {
        return tooltip;
    }

    public Identifier getIcon() {
        return icon;
    }
}
