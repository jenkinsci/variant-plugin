package org.jenkinsci.plugins.variant;

import hudson.Extension;
import net.java.sezpoz.Indexable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Works like {@link Extension} except the activation of the extension is contingent
 * on the presence of specific requirePlugins and/or classes.
 *
 * @author Kohsuke Kawaguchi
 */
@Indexable
@Retention(RUNTIME)
@Target({TYPE,METHOD,FIELD})
@Documented
public @interface OptionalExtension {
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
