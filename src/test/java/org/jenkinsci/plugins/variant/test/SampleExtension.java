package org.jenkinsci.plugins.variant.test;

import net.java.sezpoz.Indexable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * @author Kohsuke Kawaguchi
 */
@Indexable
@Retention(RUNTIME)
@Target({TYPE,METHOD,FIELD})
@Documented
public @interface SampleExtension {
    double ordinal() default 0;
    boolean optional() default false;
}
