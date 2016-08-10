package org.jenkinsci.plugins.variant;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;

import hudson.Extension;
import hudson.ExtensionFinder.GuiceExtensionAnnotation;
import hudson.PluginWrapper;
import jenkins.model.Jenkins;

/**
 * Processes extensions marked with {@link OptionalExtension} and decides when they are activated.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class OptionalExtensionProcessor extends GuiceExtensionAnnotation<OptionalExtension> {
    public OptionalExtensionProcessor() {
        super(OptionalExtension.class);
    }

    @Override
    protected double getOrdinal(OptionalExtension annotation) {
        return annotation.ordinal();
    }

    /**
     * If the trigger condition is not met, we filter it by not making the extension active, which means extensions
     * could always be non-optional as far as {@link GuiceExtensionAnnotation} is concerned. But in some situations, the
     * class cannot even be loaded which would make {@link GuiceExtensionAnnotation} to fail, so we need to also make it
     * optional.
     * <p>
     * See <a href="https://issues.jenkins-ci.org/browse/JENKINS-37317">JENKINS-37317</a>
     * 
     */
    @Override
    protected boolean isOptional(OptionalExtension annotation) {
        return true;
    }

    /**
     * Go up the scope chain (method > class > package > ...)
     * and make sure any {@link OptionalExtension}s we encounter
     * are satisified.
     */
    @Override
    protected boolean isActive(AnnotatedElement e) {
        for (; e != null; e = getParentOf(e)) {
            try {
                OptionalExtension a = e.getAnnotation(OptionalExtension.class);
                if (a != null && !isActive(a)) {
                    return false;
                }
            } catch (ArrayStoreException e1) {
                // In this case the annotation is referencing a non existent class, make the extension inactive it is
                // due to the use of requiredClasses
                // see http://bugs.java.com/view_bug.do?bug_id=7183985
                return false;
            }

            try {
                OptionalPackage b = e.getAnnotation(OptionalPackage.class);
                if (b != null && !isActive(b)) {
                    return false;
                }
            } catch (ArrayStoreException e1) {
                // In this case the annotation is referencing a non existent class, make the extension inactive it is
                // due to the use of requiredClasses
                // see http://bugs.java.com/view_bug.do?bug_id=7183985
                return false;
            }
        }

        return true;
    }

    // this function and isActive(OptionalPackage) should be kept identical
    private boolean isActive(OptionalExtension a) {
        try {
            a.requireClasses();
        } catch (ArrayStoreException x) {
            //In this case the annotation is referencing a non existent class, make the extension inactive
            // see http://bugs.java.com/view_bug.do?bug_id=7183985
            return false;
        } catch (TypeNotPresentException x) {
            return false;
        }

        for (String name : a.requirePlugins()) {
            PluginWrapper p = Jenkins.getInstance().pluginManager.getPlugin(name);
            if (p == null || !p.isActive()) return false;
        }

        for (String name : a.requireVariants()) {
            if (!VariantSet.INSTANCE.contains(name)) return false;
        }
        return true;
    }

    // this function and isActive(OptionalExtension) should be kept identical
    private boolean isActive(OptionalPackage a) {
        try {
            a.requireClasses();
        } catch (ArrayStoreException x) {
            //In this case the annotation is referencing a non existent class, make the extension inactive
            // see http://bugs.java.com/view_bug.do?bug_id=7183985
            return false;
        } catch (TypeNotPresentException x) {
            return false;
        }

        for (String name : a.requirePlugins()) {
            PluginWrapper p = Jenkins.getInstance().pluginManager.getPlugin(name);
            if (p == null || !p.isActive()) return false;
        }

        for (String name : a.requireVariants()) {
            if (!VariantSet.INSTANCE.contains(name)) return false;
        }
        return true;
    }

    /**
     * Go up the chain of scope by one.
     *
     * @return null if we hit the root.
     */
    private AnnotatedElement getParentOf(AnnotatedElement e) {
        if (e instanceof Member)
            return ((Member)e).getDeclaringClass();
        if (e instanceof  Class)
            return ((Class)e).getPackage();
        /*
        if (e instanceof Package)
            it is not possible to make recursive parent package look-up works, because in Java,
            package is only defined when a class is loaded inside it.
        */

        return null;
    }
}
