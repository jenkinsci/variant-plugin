package org.jenkinsci.plugins.variant;

import hudson.ExtensionFinder.GuiceExtensionAnnotation;
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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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

    @Test
    @Issue("JENKINS-58302")
    void testCombiningExtensionAndOptionalExtensionWarns(JenkinsRule j) throws Exception {
        List<LogRecord> records = new ArrayList<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                records.add(record);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
        handler.setLevel(Level.ALL);
        Logger logger = Logger.getLogger(OptionalExtensionProcessor.class.getName());
        Level previousLevel = logger.getLevel();
        logger.setLevel(Level.ALL);
        logger.addHandler(handler);
        try {
            GuiceExtensionAnnotation<OptionalExtension> processor = new OptionalExtensionProcessor();
            Method isActive = GuiceExtensionAnnotation.class.getDeclaredMethod("isActive", AnnotatedElement.class);
            isActive.setAccessible(true);
            boolean active = (boolean) isActive.invoke(processor, Negative7.class);

            assertFalse(active, "Negative7 should be considered inactive by the @OptionalExtension check");
            assertTrue(records.stream().anyMatch(r -> r.getLevel().equals(Level.WARNING)
                            && r.getMessage() != null
                            && r.getMessage().contains("@Extension and @OptionalExtension")),
                    "A warning should be logged when @Extension and @OptionalExtension are combined");
        } finally {
            logger.removeHandler(handler);
            logger.setLevel(previousLevel);
        }
    }

}
