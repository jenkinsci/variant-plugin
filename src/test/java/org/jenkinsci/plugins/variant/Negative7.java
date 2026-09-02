package org.jenkinsci.plugins.variant;

import hudson.Extension;
import org.jvnet.hudson.test.Issue;

/**
 * Combining the plain {@link Extension} annotation with {@link OptionalExtension} is a mistake:
 * {@link OptionalExtension} is a full replacement for {@link Extension} and already causes this
 * class to be indexed. Doing both causes Jenkins core to also discover this class through the
 * plain {@link Extension} annotation, which does not honor {@link OptionalExtension#requirePlugins()}.
 *
 * @see <a href="https://issues.jenkins-ci.org/browse/JENKINS-58302">JENKINS-58302</a>
 */
@Extension
@OptionalExtension(requirePlugins = "no-such-thing")
@Issue("JENKINS-58302")
public class Negative7 extends Base {
}
