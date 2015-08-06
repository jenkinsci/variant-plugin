# Variant plugin
This plugin allows Jenkins plugins to behave differently based on the "environment" it is running in.

## Optional Extension
With this plugin, you define environment sensitive extension by marking your extension as `@OptionalExtension`
that specifies the condition of activation.
 
```
@OptionalExtension(requirePlugins="git")
public class Foo implements ... {
   ...
}
```

You can specify the existence of either a class or a plugin as a requirement.

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
