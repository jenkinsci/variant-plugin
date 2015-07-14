package org.jenkinsci.plugins.multimode;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Annotates subtypes of {@link Variant}.
 *
 * @author Kohsuke Kawaguchi
 * @see MultiModeExtension
 */
@Retention(RUNTIME)
@Target(TYPE)
@Documented
public @interface VariantAnnotation {
    /**
     * Mode name, as specified by {@code JENKINS_VARIANT} environment variable.
     */
    String value();
}
