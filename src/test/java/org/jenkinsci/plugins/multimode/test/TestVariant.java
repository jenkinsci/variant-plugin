package org.jenkinsci.plugins.multimode.test;

import org.jenkinsci.plugins.multimode.Variant;
import org.jenkinsci.plugins.multimode.VariantAnnotation;

/**
 * @author Kohsuke Kawaguchi
 */
@VariantAnnotation("test")
public class TestVariant extends Variant {
    private TestVariant() {
        // no instantiation
    }
}
