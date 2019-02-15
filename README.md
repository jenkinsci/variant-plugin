# Variant plugin
This plugin allows Jenkins plugins to behave differently based on the "environment" it is running in.

See also  http://plugins.jenkins.io/variant

## Optional Extension
With this plugin, you define environment sensitive extension by marking your extension as `@OptionalExtension`
that specifies the condition of activation.

```
@OptionalExtension(requirePlugins="git")
public class Foo implements ... {
   ...
}
```

Optional extensions can be activated by one of the following conditions:

* Presence of plugins, identified by their short names (like "git")
* Presence of a class
* Presence of a "variant" in the environment.

### Package for optional extension


## Activating variants
Active variants can be selected in several ways. Multiple variants can be independently activated.
All of the variants activated in these different ways are combined together to form the effective variant set.

* Via environment variable `JENKINS_VARIANT`. This is a whitespace separated list of variant names.
  This is intended for users to configure through init scripts like `/etc/default/...`

* Files in the `$JENKINS_HOME/variants` directory. For example, if there's a file named
  `$JENKINS_HOME/variants/cjoc`, then the `cjoc` variant gets activated. This is also intended
  for users.

* `META-INF/MANIFEST.MF` of `jenkins.war` can have the `Jenkins-Variant` attribute in its main section.
  This whitespace separated list of variant names is intended to be used by people who create
  custom distributions of Jenkins.


## Why
This addition will allow more controlled extension enablement beyond `@Extension(optional=true)`. For example,
it is often common for a plugin to define some additional integration extension that only kicks in when another
plugin is present.

Beyond that, at CloudBees, we reuse Jenkins as the platform for other Jenkins-like services. Those are built
on the same Jenkins core, but they do not build and instead do other things, such as "CloudBees Jenkins Operations
Center."

This mechanism also lets us define multi-modal plugin that can run in such variants and behave differently.
In this way, we can use the same plugin across different variants of Jenkins, so that users can think of this as
installing one plugin in the whole network of Jenkins variants.
