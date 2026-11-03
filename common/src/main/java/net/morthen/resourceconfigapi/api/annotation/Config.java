package net.morthen.resourceconfigapi.api.annotation;


import net.morthen.resourceconfigapi.api.ConfigRegistry;
import net.morthen.resourceconfigapi.api.ConfigType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as config. Every {@code public static} field annotated with {@link ConfigEntry}
 * becomes an option of the config. Register the class with
 * {@link ConfigRegistry#register(Class, String)}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Config {

    /**
     * Name of the config file without extension, it can be different from the class name.<br>
     * Only lowercase letters, digits, {@code _}, {@code -} and {@code .} are allowed, because the name
     * is used for the file, the config id and the translation keys:
     * <ul>
     *     <li>file: {@code config/{modId}/{type}/{fileName}.json}</li>
     *     <li>label: {@code config.{modId}.{fileName}.label}</li>
     *     <li>tooltip: {@code config.{modId}.{fileName}.tooltip}</li>
     * </ul>
     * @return the file name
     */
    String fileName();

    ConfigType type();
}
