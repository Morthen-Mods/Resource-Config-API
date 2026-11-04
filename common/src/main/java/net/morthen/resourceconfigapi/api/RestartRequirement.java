package net.morthen.resourceconfigapi.api;

/** What has to happen before a changed option takes effect. */
public enum RestartRequirement {
    /** The change is applied immediately. */
    NONE,
    /** The world has to be rejoined. */
    WORLD,
    /** The game has to be restarted. */
    GAME
}
