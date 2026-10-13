package net.morthen.resource_config_api.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Config {

    /**
     * Name of the config file. It is also used for the translation keys
     * ({@code config.<modId>.<fileName>}) and, lowercased, for the config id,
     * so it must be a valid identifier path.
     */
    String fileName();

    ConfigType type();
}
