package net.morthen.resourceconfigapi.api;

import java.util.Locale;

/**
 * Side a config belongs to. Currently the type only decides the folder the file is placed in
 * and the tab it is shown in, configs are not synced between server and client.
 */
public enum ConfigType {
    /** Settings that only affect the client, like rendering or controls. */
    CLIENT,
    /** Settings used by both sides, this includes everything that is relevant for the server. */
    COMMON;

    /** @return the folder name used for the config files of this type */
    public String getDirectoryName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
