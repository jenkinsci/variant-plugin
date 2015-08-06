package org.jenkinsci.plugins.variant;

import hudson.model.RootAction;

/**
 * @author Kohsuke Kawaguchi
 */
public class Base implements RootAction {
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
