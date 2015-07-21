package org.jenkinsci.plugins.multimode;

import jenkins.model.Jenkins;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.io.IOUtils.*;

/**
 * Keeps track of active variants.
 *
 * @author Kohsuke Kawaguchi
 */
class VariantSet {
    // not final for tests
    /*package*/ static VariantSet INSTANCE = VariantSet.makeDefault();

    private final Set<String> activeVariants = new HashSet<String>();

    VariantSet(String... values) {
        activeVariants.addAll(Arrays.asList(values));
    }

    static VariantSet makeDefault() {
        VariantSet v = new VariantSet();
        v.activeVariants.addAll(v.parseVariants());
        return v;
    }

    /**
     * Decide if the given custom extension annotation is active in this JVM or not.
     */
    protected boolean isActive(MultiModeExtensionProcessor t) {
        return activeVariants.contains(t.name);
    }

    /**
     * Determines the currently active variants.
     */
    private Set<String> parseVariants() {
        Set<String> values = new HashSet<String>();

        addVariants(values, System.getenv("JENKINS_VARIANT"));

        Jenkins j = Jenkins.getInstance();
        if (j!=null) {
            InputStream in = j.servletContext.getResourceAsStream("META-INF/MANIFEST.MF");
            try {
                if (in != null) {
                    Manifest m = new Manifest(in);
                    addVariants(values, m.getMainAttributes().getValue("Jenkins-Variant"));
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to parse Jenkins-Variants from manifest", e);
            } finally {
                closeQuietly(in);
            }
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

    private static final Logger LOGGER = Logger.getLogger(VariantSet.class.getName());
}
