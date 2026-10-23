package net.morthen.resourceconfigapi.api.annotations;

import net.morthen.resourceconfigapi.api.ConfigType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
    String fileName();
    ConfigType type();
}
