# Variant plugin
This plugin allows Jenkins plugins to behave in multiple modes, depending on variants of Jenkins they
are installed on.

## Why
At CloudBees, we reuse Jenkins as the platform for other Jenkins-like services. Those are built on the same
Jenkins core, but they do not build and instead do other things, such as "CloudBees Jenkins Operations Center."

As we do this, We found that it simplifies user experience to be able to install the same plugin
across different variants of Jenkins. But those plugins need to behave differently depending on where
they are running.

Jenkins Variant plugin is a library plugin that makes such multi-modal plugins possible.

## How
### Defining variant
First, you define a 'variant', which represents a specific variant of Jenkins. You do this
by defining an annotation that acts like `@Extension`, and a processor that recognizes this annotation.

The annotation must be `@Indexable` and `@Retention(RUNTIME)`, and it has to have the `ordinal`
and `optional` methods:

```
@Indexable
@Retention(RUNTIME)
@Target({TYPE,METHOD,FIELD})
@Documented
public @interface CjocExtension {
    // these two parameters act the same as @Extension
    double ordinal() default 0;
    boolean optional() default false;
}
```

The processor is defined as follows. Aside from specifying the type of the annotation you defined
above, you also specify the name of the variant. In this example, the variant is named "cjoc":
```
@Extension
public class CjocExtensionProcessor extends VariantExtensionProcessor<CjocExtension> {
    public CjocExtensionProcessor() {
        super(CjocExtension.class, "cjoc");
    }
}
```

A variant is usually defined in a library plugin that other plugins use as a dependency.


## Declaring variant-specific extension
Once a variant is defined, you can mark components with a variant-specific annotation instead of
`@Extension`:

```
@CjocExtension
public class Foo implements RootAction {
   ...
}
```

When a component is annotated with `@Extension`, Jenkins always activates that regardless of
the current variants. But when a variant-specific annotation is used, a component is activated
only when the specific variant is active.

## Programmatically checking current active variants
Alternatively, for advanced use cases you can programmatically check whether or not a specific
variant is active:

```
if (VariantSet.getInstance().contains("cjoc")) {
    ...
}
```

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
