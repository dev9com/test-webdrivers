package com.dev9.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface MethodDriver {

    /**
     * Whether the WebDriver will be initialized at runtime
     */
    boolean enabled() default true;

    /**
     * Given build() contains Before.Method the following list of
     * methods will not have a WebDriver instance created
     */
    String[] excludeMethods() default {};
}
