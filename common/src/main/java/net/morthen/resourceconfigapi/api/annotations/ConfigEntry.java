package net.morthen.resourceconfigapi.api.annotations;

import net.morthen.resourceconfigapi.api.RestartRequirement;

public @interface ConfigEntry {
    String category();
    RestartRequirement restartRequirement() default RestartRequirement.NONE;
    String translation();
}
