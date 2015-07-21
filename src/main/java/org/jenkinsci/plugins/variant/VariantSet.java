package org.jenkinsci.plugins.variant;

import jenkins.model.Jenkins;

import java.io.File;
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
// can't do @Extension because this needs to be instantiated before the IoC container gets initialized
public class VariantSet {
    // not final for tests
    /*package*/ static VariantSet INSTANCE = VariantSet.makeDefault();

    private final Set<String> activeVariants = new HashSet<String>();

    VariantSet(String... values) {
        activeVariants.addAll(Arrays.asList(values));
    }

    /**
     * Gets a singleton instance.
     */
    public VariantSet getInstance() {
        return INSTANCE;
    }

    static VariantSet makeDefault() {
        VariantSet v = new VariantSet();
        v.activeVariants.addAll(v.parseVariants());
        return v;
    }

    /**
     * Decide if the given custom extension annotation is active in this JVM or not.
     */
    protected boolean isActive(VariantExtensionProcessor t) {
        return contains(t.name);
    }

    /**
     * Determines if a variant of the given name is active in this JVM.
     */
    public boolean contains(String name) {
        return activeVariants.contains(name);
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

            File[] children = new File(j.getRootDir(),"variants").listFiles();
            if (children!=null) {
                for (File ch : children) {
                    values.add(ch.getName());
                }
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
