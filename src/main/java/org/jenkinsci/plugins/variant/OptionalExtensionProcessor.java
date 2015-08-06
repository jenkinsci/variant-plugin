package org.jenkinsci.plugins.variant;

import hudson.Extension;
import hudson.ExtensionFinder.GuiceExtensionAnnotation;
import hudson.PluginWrapper;
import jenkins.model.Jenkins;

import java.lang.reflect.AnnotatedElement;

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

    @Override
    protected boolean isActive(AnnotatedElement e) {
        OptionalExtension a = e.getAnnotation(OptionalExtension.class);
        if (a==null)    return false;   // index is stale

        try {
            a.requireClasses();
        } catch (ArrayStoreException x) {
            // see http://bugs.java.com/view_bug.do?bug_id=7183985
            return false;
        } catch (TypeNotPresentException x) {
            return false;
        }

        for (String name : a.requirePlugins()) {
            PluginWrapper p = Jenkins.getInstance().pluginManager.getPlugin(name);
            if (p==null || !p.isActive())    return false;
        }

        for (String name : a.requireVariants()) {
            if (!VariantSet.INSTANCE.contains(name))    return false;
        }

        return true;
    }
}
