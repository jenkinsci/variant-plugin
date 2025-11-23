package org.jenkinsci.plugins.variant;

import hudson.model.Action;
import org.jenkinsci.plugins.variant.pkg.Negative5;
import org.jenkinsci.plugins.variant.pkg.Positive5;
import org.jenkinsci.plugins.variant.pkg2.Negative6;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Kohsuke Kawaguchi
 */
@WithJenkins
class VariantTest {

    @BeforeAll
    static void setUp() {
        VariantSet.INSTANCE = new VariantSet("test");
    }

    @AfterAll
    static void tearDown() {
        VariantSet.INSTANCE = new VariantSet();
    }

    @Test
    void test(JenkinsRule j) {
        List<Class<?>> classes = new ArrayList<>();
        for (Action a : j.jenkins.getActions()) {
            classes.add(a.getClass());
        }
        assertTrue(classes.contains(Positive1.class));
        assertTrue(classes.contains(Positive2.class));
        assertTrue(classes.contains(Positive3.class));
        assertTrue(classes.contains(Positive5.class));
	    assertFalse(classes.contains(Negative1.class));
	    assertFalse(classes.contains(Negative2.class));
	    assertFalse(classes.contains(Negative5.class));
	    assertFalse(classes.contains(Negative6.class));
    }

    @Test
    @Issue("JENKINS-37317")
    void testRequiredClass(JenkinsRule j) {
        List<Class<?>> classes = new ArrayList<>();
        for (Action a : j.jenkins.getActions()) {
            classes.add(a.getClass());
        }
	    assertFalse(classes.contains(Negative3.class), "Negative 3 should not exist");

        for (Class<?> klass : classes) {
            System.out.println(klass.getCanonicalName());
	        assertNotEquals("org.jenkinsci.plugins.variant.Negative4", klass.getCanonicalName(), "Negative 4 should not exist");
        }

        assertTrue(classes.contains(Positive4.class));
    }

}
