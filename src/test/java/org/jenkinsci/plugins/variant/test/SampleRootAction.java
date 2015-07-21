package org.jenkinsci.plugins.variant.test;

import hudson.model.RootAction;
import jenkins.model.Jenkins;

import javax.inject.Inject;

/**
 * @author Kohsuke Kawaguchi
 */
@SampleExtension
public class SampleRootAction implements RootAction {
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
