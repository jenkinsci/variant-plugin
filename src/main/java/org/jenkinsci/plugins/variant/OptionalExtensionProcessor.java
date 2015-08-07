package org.jenkinsci.plugins.variant;

import hudson.Extension;
import hudson.ExtensionFinder.GuiceExtensionAnnotation;
import hudson.PluginWrapper;
import jenkins.model.Jenkins;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;

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
     * If the trigger condition is not met, we filter it by not making the extension active,
     * which means extensions are always non-optional as far as {@link GuiceExtensionAnnotation}
     * is concerned.
     */
    @Override
    protected boolean isOptional(OptionalExtension annotation) {
        return false;
    }

    /**
     * Go up the scope chain (method > class > package > ...)
     * and make sure any {@link OptionalExtension}s we encounter
     * are satisified.
     */
    @Override
    protected boolean isActive(AnnotatedElement e) {
        System.out.println(e);
        for (; e!=null; e=getParentOf(e)) {
            OptionalExtension a = e.getAnnotation(OptionalExtension.class);
            if (a!=null && !isActive(a))    return false;

            OptionalPackage b = e.getAnnotation(OptionalPackage.class);
            if (b!=null && !isActive(b))    return false;
        }

        return true;
    }

    // this function and isActive(OptionalPackage) should be kept identical
    private boolean isActive(OptionalExtension a) {
        try {
            a.requireClasses();
        } catch (ArrayStoreException x) {
            // see http://bugs.java.com/view_bug.do?bug_id=7183985
            return true;
        } catch (TypeNotPresentException x) {
            return true;
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
            // see http://bugs.java.com/view_bug.do?bug_id=7183985
            return true;
        } catch (TypeNotPresentException x) {
            return true;
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
        if (e instanceof Package) {
            String name = ((Package) e).getName();

            while (true) {
                int idx = name.lastIndexOf('.');
                if (idx<0)      return null;
                name = name.substring(0, idx);
                Package pkg = Package.getPackage(name);
                if (pkg!=null)  return pkg;
            }
        }

        return null;
    }
}
