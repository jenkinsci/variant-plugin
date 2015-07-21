package org.jenkinsci.plugins.variant;

import hudson.Extension;

/**
 * Glue code that activates {@link VanillaOnlyExtension}.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class VanillaOnlyExtensionProcessor extends VariantExtensionProcessor<VanillaOnlyExtension> {
    public VanillaOnlyExtensionProcessor() {
        super(VanillaOnlyExtension.class, VanillaOnlyExtension.NAME);
    }
}
