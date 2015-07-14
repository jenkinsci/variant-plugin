package org.jenkinsci.plugins.multimode;

import hudson.ExtensionFinder.GuiceExtensionAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Makes Jenkins recognize annotations with {@link OnlyFor}.
 *
 * @author Kohsuke Kawaguchi
 */
class MultiModeExtensionProcessor<T extends Annotation> extends GuiceExtensionAnnotation<T> {
    /**
     * Factory method for better compiler type inference.
     */
    static <T extends Annotation> MultiModeExtensionProcessor<T> make(Class<T> t) {
        return new MultiModeExtensionProcessor<T>(t);
    }

    private final Method ordinal;
    private final Method optional;

    public MultiModeExtensionProcessor(Class<T> type) {
        super(type);

        ordinal  = getMethod("ordinal",double.class);
        optional = getMethod("optional", boolean.class);
    }

    private Method getMethod(String name, Class returnType) {
        try {
            Method m = annotationType.getMethod(name);
            Class<?> actual = ordinal.getReturnType();
            if (actual!=returnType)
                throw new IllegalStateException(m+" expected to return "+returnType+" but found "+actual);
            return m;
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(annotationType+" should have a method named "+name);
        }
    }

    @Override
    protected double getOrdinal(T a) {
        try {
            return (Double)ordinal.invoke(a);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);    // we use Class.getMethod that only returns public methods
        } catch (InvocationTargetException e) {
            eat(e.getTargetException());
            throw new AssertionError(e);
        }
    }

    @Override
    protected boolean isOptional(T a) {
        try {
            return (Boolean)optional.invoke(a);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);    // we use Class.getMethod that only returns public methods
        } catch (InvocationTargetException e) {
            eat(e.getTargetException());
            throw new AssertionError(e);
        }
    }

    @Override
    protected boolean isActive(AnnotatedElement e) {
        return true;
    }

    private void eat(Throwable t) {
        if (t instanceof RuntimeException)
            throw (RuntimeException) t;
        if (t instanceof Error)
            throw (Error) t;
        // don't know what to do
    }
}
