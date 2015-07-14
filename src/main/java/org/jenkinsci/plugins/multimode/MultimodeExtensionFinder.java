package org.jenkinsci.plugins.multimode;

import hudson.Extension;
import hudson.ExtensionComponent;
import hudson.ExtensionFinder;
import hudson.model.Hudson;
import jenkins.ExtensionComponentSet;
import jenkins.ExtensionRefreshException;
import jenkins.model.Jenkins;
import org.jvnet.hudson.annotation_indexer.Index;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.io.IOUtils.*;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class MultimodeExtensionFinder extends ExtensionFinder {
    /**
     * Currently active variants parsed from the environment variable.
     */
    /*package*/ final Set<String> activeVariants = parseVariants();

    @Override
    public ExtensionComponentSet refresh() throws ExtensionRefreshException {
        return ExtensionComponentSet.EMPTY; // TODO
    }

    @Override
    public <T> Collection<ExtensionComponent<T>> find(Class<T> type, Hudson jenkins) {
        if (type==GuiceExtensionAnnotation.class) {
            try {
                List<ExtensionComponent<T>> l = new ArrayList<ExtensionComponent<T>>();
                for (Class<?> t : Index.list(OnlyFor.class, jenkins.pluginManager.uberClassLoader,Class.class)) {
                    Class<? extends Annotation> at = t.asSubclass(Annotation.class);
                    if (isActive(at)) {
                        l.add(new ExtensionComponent<T>(type.cast(MultiModeExtensionProcessor.make(at))));
                    }
                }
                return l;
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to list up @MultiModeExtensionAnnotation",e);
                // fall through and return empty list
            }
        }
        return Collections.emptyList();
    }

    /**
     * Given an annotation type with {@link OnlyFor},
     * decide if this extension is active in the current JVM or not.
     */
    protected boolean isActive(Class<? extends Annotation> t) {
        OnlyFor a = t.getAnnotation(OnlyFor.class);
        if (a==null)
            throw new IllegalStateException("Expecting to see @MultiModeExtensionAnnotation on "+t+" but didn't find it");
        return activeVariants.contains(a.value());
    }

    /**
     * Determines the currently active variants.
     */
    private Set<String> parseVariants() {
        Set<String> values = new HashSet<String>();

        addVariants(values, System.getenv("JENKINS_VARIANT"));
        addVariants(values, PROBE_VARIANT);

        InputStream in = Jenkins.getInstance().servletContext.getResourceAsStream("META-INF/MANIFEST.MF");
        try {
            if (in!=null) {
                Manifest m = new Manifest(in);
                addVariants(values, m.getMainAttributes().getValue("Jenkins-Variant"));
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to parse Jenkins-Variants from manifest", e);
        } finally {
            closeQuietly(in);
        }

        if (values.isEmpty())
            values.add(VanillaOnlyExtension.NAME);

        return values;
    }

    private void addVariants(Set<String> values, String e) {
        if (e!=null) {
            for (String t : e.split(" +")) {
                t = t.trim();
                if (t.length() > 0)
                    values.add(t);
            }
        }
    }

    /**
     * Additional variant used during tests
     */
    /*package*/ static String PROBE_VARIANT;

    private static final Logger LOGGER = Logger.getLogger(MultimodeExtensionFinder.class.getName());
}
