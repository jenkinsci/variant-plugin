package org.jenkinsci.plugins.multimode;

import hudson.Extension;
import net.java.sezpoz.Indexable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks an extension that is only active for specific variants.
 *
 * <p>
 * In some context, it is convenient for the same plugin
 * to behave differently depending on the current "variant" of the Jenkins (such as
 * CloudBees Jenkins Operations Center vs CloudBees Jenkins Enterprise.) This
 * {@link MultiModeExtension} works like {@link Extension} but with the additional control
 * to selectively enable a group of extensions.
 *
 * @author Kohsuke Kawaguchi
 * @see Extension
 */
@Indexable
@Retention(RUNTIME)
@Target({TYPE, FIELD, METHOD})
@Documented
public @interface MultiModeExtension {
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

    Class<? extends Variant>[] value();
}
