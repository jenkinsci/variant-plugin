package org.jenkinsci.plugins.variant;

import hudson.model.Action;
import org.jenkinsci.plugins.variant.test.SampleRootAction;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * @author Kohsuke Kawaguchi
 */
public class VariantTest extends Assert {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @BeforeClass
    public static void setUp() {
        VariantSet.INSTANCE = new VariantSet("test");
    }

    @AfterClass
    public static void tearDown() {
        VariantSet.INSTANCE = new VariantSet();
    }

    @Test
    public void test() {
        for (Action a : j.jenkins.getActions()) {
            if (a.getClass()==SampleRootAction.class)
                return; // test pass
        }
        fail("Expecting to see TestRootAction");
    }
}
