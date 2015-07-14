package org.jenkinsci.plugins.multimode;

import com.trilead.ssh2.util.IOUtils;
import hudson.Extension;
import hudson.ExtensionFinder.GuiceExtensionAnnotation;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Makes Jenkins recognize {@link MultiModeExtension}.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class MultiModeExtensionProcessor extends GuiceExtensionAnnotation<MultiModeExtension> {
    public MultiModeExtensionProcessor() {
        super(MultiModeExtension.class);
    }

    @Override
    protected double getOrdinal(MultiModeExtension a) {
        return a.ordinal();
    }

    @Override
    protected boolean isOptional(MultiModeExtension annotation) {
        return annotation.optional();
    }

    @Override
    protected boolean isActive(AnnotatedElement e) {
        MultiModeExtension a = e.getAnnotation(MultiModeExtension.class);
        for (Class<? extends Variant> c : a.value()) {
            if (isActive(c))
                return true;
        }
        return false;
    }

    private boolean isActive(Class<? extends Variant> c) {
        Boolean b = ACTIVE.get(c);
        if (b!=null)        return b;

        VariantAnnotation a = c.getAnnotation(VariantAnnotation.class);
        if (a==null)
            throw new IllegalArgumentException("Variant subtype "+c+" does not have @VariantAnnotation");
        b = VARIANTS.contains(a.value());
        ACTIVE.put(c,b);

        return b;
    }

    private static final Logger LOGGER = Logger.getLogger(MultiModeExtensionProcessor.class.getName());

    /**
     * Once we identify if a {@link Variant} is active or not, we remember it here for efficiency.
     */
    /*package*/ static final ConcurrentMap<Class,Boolean> ACTIVE = new ConcurrentHashMap<Class, Boolean>();

    /**
     * Currently active variants parsed from the environment variable.
     */
    /*package*/ static final Set<String> VARIANTS = parseVariants();

    /**
     * Determines the currently active variants.
     */
    private static Set<String> parseVariants() {
        Set<String> values = new HashSet<String>();

        addVariants(values, System.getenv("JENKINS_VARIANT"));

        InputStream in = Jenkins.getInstance().servletContext.getResourceAsStream("META-INF/MANIFEST.MF");
        try {
            Manifest m = new Manifest(in);
            addVariants(values, m.getMainAttributes().getValue("Jenkins-Variant"));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to parse Jenkins-Variants from manifest", e);
        } finally {
            IOUtils.closeQuietly(in);
        }

        if (values.isEmpty())
            values.add("none");

        return values;
    }

    private static void addVariants(Set<String> values, String e) {
        if (e!=null) {
            for (String t : e.split(" +")) {
                t = t.trim();
                if (t.length() > 0)
                    values.add(t);
            }
        }
    }
}
