package org.jenkinsci.plugins.variant.test;

import hudson.Extension;
import org.jenkinsci.plugins.variant.VariantExtensionProcessor;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class SampleExtensionProcessor extends VariantExtensionProcessor<SampleExtension> {
    public SampleExtensionProcessor() {
        super(SampleExtension.class,"test");
    }
}
