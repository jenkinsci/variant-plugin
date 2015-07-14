package org.jenkinsci.plugins.multimode.test;

import hudson.model.RootAction;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.multimode.MultiModeExtension;

import javax.inject.Inject;

/**
 * @author Kohsuke Kawaguchi
 */
@MultiModeExtension(TestVariant.class)
public class TestRootAction implements RootAction {
    @Inject
    Jenkins j;

    @Override
    public String getIconFileName() {
        return "gear.png";
    }

    @Override
    public String getDisplayName() {
        return "Test";
    }

    @Override
    public String getUrlName() {
        return "test";
    }
}
