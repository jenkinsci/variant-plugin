package org.jenkinsci.plugins.multimode;

import hudson.Extension;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class VanillaOnlyExtensionProcessor extends MultiModeExtensionProcessor<VanillaOnlyExtension> {
    public VanillaOnlyExtensionProcessor() {
        super(VanillaOnlyExtension.class, VanillaOnlyExtension.NAME);
    }
}
