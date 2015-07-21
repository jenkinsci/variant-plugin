package org.jenkinsci.plugins.variant;

import net.java.sezpoz.Indexable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Allows extensions to activate only when no variant is explicitly specified. That is,
 * a normal plain-vanilla Jenkins.
 *
 * @author Kohsuke Kawaguchi
 */
@Indexable
@Retention(RUNTIME)
@Target({TYPE,METHOD,FIELD})
@Documented
public @interface VanillaOnlyExtension {
    /**
     * Used for sorting extensions.
     *
     * Extensions will be sorted in the descending order of the ordinal.
     * This is a rather poor approach to the problem, so its use is generally discouraged.
     */
    double ordinal() default 0;

    /**
     * If an extension is optional, don't log any class loading errors when reading it.
     */
    boolean optional() default false;

    /**
     * Variant name for this annotation. This is a special pseudo-name because
     * it gets activated when no other variants are given.
     */
    String NAME = "vanilla";
}
