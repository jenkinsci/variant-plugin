package org.jenkinsci.plugins.variant;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Works in conjunction with {@link OptionalExtension} to allow you to turn
 * the entire packages conditional.
 *
 * <p>
 * This annotation is only effective for classes that are direct members of the package.
 * For example, if you put this annotation on {@code com.example}, {@code com.example.sub.Foo}
 * will not be affected. This is due to the way Java package works.
 *
 * @author Kohsuke Kawaguchi
 */
@Retention(RUNTIME)
@Target({PACKAGE})
@Documented
public @interface OptionalPackage {
    /**
     * Short names of the plugins that are required to trigger this extension.
     */
    String[] requirePlugins() default {};

    /**
     * Classes that are required to activate this extension.
     */
    Class[] requireClasses() default {};

    /**
     * Variants that are required to activate this extension.
     */
    String[] requireVariants() default {};
}
