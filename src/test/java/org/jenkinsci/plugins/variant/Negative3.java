package org.jenkinsci.plugins.variant;

import org.jenkinsci.plugins.Beer;
import org.jvnet.hudson.test.Issue;

@OptionalExtension(requireClasses=Beer.class)
@Issue("JENKINS-37317")
public class Negative3 extends Base {
}
