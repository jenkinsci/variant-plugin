package org.jenkinsci.plugins.variant;

import hudson.model.Action;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
public class VariantTest extends Assert {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void test() {
        List<Class> classes = new ArrayList<Class>();
        for (Action a : j.jenkins.getActions()) {
            classes.add(a.getClass());
        }
        assertTrue(classes.contains(Positive1.class));
        assertTrue(classes.contains(Positive2.class));
        assertTrue(!classes.contains(Negative1.class));
    }
}
