package org.jenkinsci.plugins.multimode;

import hudson.model.Action;
import org.jenkinsci.plugins.multimode.test.TestRootAction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * @author Kohsuke Kawaguchi
 */
public class MultiModeTest extends Assert {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Before
    public void setUp() {
        MultiModeExtensionProcessor.ACTIVE.clear();
        MultiModeExtensionProcessor.VARIANTS.add("test");
    }

    @Test
    public void test() {
        for (Action a : j.jenkins.getActions()) {
            if (a.getClass()== TestRootAction.class)
                return; // test pass
        }
        fail("Expecting to see TestRootAction");
    }
}
