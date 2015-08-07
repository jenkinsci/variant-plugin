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
 * @author Kohsuke Kawaguchi
 */
@Retention(RUNTIME)
@Target({PACKAGE})
@Documented
public @interface OptionalPackage {
    /**
     * Used for sorting extensions.
     *
     * Extensions will be sorted in the descending order of the ordinal.
     * This is a rather poor approach to the problem, so its use is generally discouraged.
     */
    double ordinal() default 0;

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
