package net.morthen.resource_config_api.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.morthen.resource_config_api.ConfigConstants;

public enum EntryImpact {
    DIRECT(ConfigConstants.DIRECT_IMPACT, "direct_impact"),
    WORLD_RESTART(ConfigConstants.WORLD_RESTART, "world_restart"),
    GAME_RESTART(ConfigConstants.GAME_RESTART, "game_restart");

    private final Component tooltip;
    private final Identifier icon;

    EntryImpact(Component tooltip, String key) {
        this.tooltip = tooltip;
        this.icon = ConfigConstants.of("icon/" + key);
    }

    public Component getTooltip() {
        return tooltip;
    }

    public Identifier getIcon() {
        return icon;
    }
}
