package org.jenkinsci.plugins.multimode.test;

import hudson.Extension;
import org.jenkinsci.plugins.multimode.MultiModeExtensionProcessor;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class SampleExtensionProcessor extends MultiModeExtensionProcessor<SampleExtension> {
    public SampleExtensionProcessor() {
        super(SampleExtension.class,"test");
    }
}
