package org.jenkinsci.plugins.multimode;

/**
 * Allows {@link MultiModeExtension} to match when no variant is specified. That is,
 * a normal ordinary Jenkins.
 *
 * @author Kohsuke Kawaguchi
 */
@VariantAnnotation("none")
public class NoVariant extends Variant {
    private NoVariant() {}
}
