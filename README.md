# QuPath extension template

This repo contains a template and instructions to help create a new extension for [QuPath](https://qupath.github.io).

It already contains two minimal extensions - one using Java, one using Groovy - so the first task is to make sure that they work.
Then, it's a matter of customizing the code to make it more useful.

> **Update!** 
> For QuPath v0.6.0 this repo switched to use Kotlin DSL for Gradle build files - 
> and also to use the [QuPath Gradle Plugin](https://github.com/qupath/qupath-gradle-plugin).
> 
> The outcome is that the build files are _much_ simpler.


## Build the extension

Building the extension with Gradle should be pretty easy - you don't even need to install Gradle separately, because the 
[Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) will take care of that.

Open a command prompt, navigate to where the code lives, and use
```bash
gradlew build
```

The built extension should be found inside `build/libs`.
You can drag this onto QuPath to install it.
You'll be prompted to create a user directory if you don't already have one.

The minimal extension here doesn't do much, but it should at least install a new command under the 'Extensions' menu in 
QuPath.

> In case your extension contains external dependencies beyond what QuPath already includes, you can create a 
> [single jar file](https://imperceptiblethoughts.com/shadow/introduction/#benefits-of-shadow) that bundles these along 
> with your extension by using
> ```bash
> gradlew shadowJar
> ```
> If you don't do that, you'll need to drag *all* the extra dependences onto QuPath to install them as well.


## Configure the extension

Edit `settings.gradle.kts` to specify which version of QuPath your extension should be compatible with, e.g.

```kotlin
qupath {
    version = "0.6.0-SNAPSHOT"
}
```

Edit `build.gradle.kts` to specify the details of your extension

```kotlin
qupathExtension {
  name = "qupath-extension-template"
  group = "io.github.qupath"
  version = "0.1.0-SNAPSHOT"
  description = "A simple QuPath extension"
  automaticModule = "io.github.qupath.extension.template"
}
```


## Run QuPath + the extension

During development, your probably want to run QuPath easily with your extension installed for debugging.

### 0. Make sure you have Java installed
You'll need to install Java first.

At the time of writing, we use a Java 21 JDK downloaded from https://adoptium.net/

> Java 21 is a 'Long Term Support' release - which is why we use it instead of the very latest version.

### 1. Get QuPath's source code
You can find instructions at https://qupath.readthedocs.io/en/stable/docs/reference/building.html

### 2. Create an `include-extra` file
Create a file called `include-extra` in the root directory of the QuPath source code (*not* the extension code!).

Set the contents of this file to:
```
[includeBuild]
/path/to/your/extension

[dependencies]
extension-group:extension-name
```
replacing the default lines where needed.

For example, to build the extension with the names given above you'd use
```
[includeBuild]
../qupath-extension-template

[dependencies]
io.github.qupath:qupath-extension-template
```

### 3. Run QuPath
Run QuPath from the command line using
```
gradlew run
```
If all goes well, QuPath should launch and you can check the *Extensions* mention to confirm the extension is installed.


## Set up in an IDE (optional)

During development, things are likely to be much easier if you work within an IDE.

QuPath itself is developed using IntelliJ, and you can import the extension template there.

The setup process is as above, and you'll need a a [Run configuration](https://www.jetbrains.com/help/idea/run-debug-configuration.html) 
to call `gradlew run`.


## Customize the extension

Now you're ready for the creative part.

You can develop the extension using either Java or Groovy - the template includes examples of both.

### Create the extension Java or Groovy file(s)

For the extension to work, you need to create at least one file that extends `qupath.lib.gui.extensions.QuPathExtension`.

There are two examples in the template, in two languages:
* **Java:** `qupath.ext.template.DemoExtension.java`.
* **Groovy:** `qupath.ext.template.DemoGroovyExtension.java`.

You can pick the one that corresponds to the language you want to use, and delete the other.

Then take your chosen file and rename it, edit it, move it to another package... basically, make it your own.

> Please **don't neglect this step!** 
> If you do, there's a chance of multiple extensions being created with the same class names... and causing confusion later.

### Update the `META-INF/services` file

For QuPath to *find* the extension later, the full class name needs to be available in `resources/META-INFO/services/qupath.lib.gui.extensions.QuPathExtensions`.

So remember to edit that file to include the class name that you actually used for your extension.

### Specify your license

Add a license file to your GitHub repo so that others know what they can and can't do with your extension.

This should be compatible with QuPath's license -- see https://github.com/qupath/qupath

## Repository configuration

### Easy install

If you follow some conventions in naming your extension and making releases, then other QuPath users will find it easy to automatically
install and update your extension!

First, we suggest you name your extension `qupath-extension-[something]`, and keep it in its own repository (named the same as the extension),
separate from other projects.

Next, when you want to publish a new version of your extension, use the `github_release.yml` workflow included in this repository.

To do so, you'd need to navigate to `Actions -> Make draft release -> Run workflow -> Run workflow` as shown in the following screenshot:

![Screenshot from 2024-03-14 18-44-42](https://github.com/alanocallaghan/qupath-extension-template/assets/10779688/4712a209-eda7-4f80-8bed-bbab20e4f50a)

This will automatically build the extension, and create a draft release containing the extension jar (and its associated sources and javadoc).
You can then navigate to `Releases` and fill out information about the release --- the version, any significant changes, etc.
Once published, users will be able to automatically install the extension as described here:
https://qupath.readthedocs.io/en/0.5/docs/intro/extensions.html#installing-extensions

### Automatic updates

To enable easy installation and automatic updates in QuPath, fill in the (**public**) GitHub owner and repository 
for the extension.

https://github.com/qupath/qupath-extension-template/blob/778f02759d8a7fe5c73f1751edd58b6494beff9f/src/main/java/qupath/ext/template/DemoExtension.java#L65-L66

### Replace this readme

Don't forget to replace the contents of this readme with your own!


## Getting help

For questions about QuPath and/or creating new extensions, please use the forum at https://forum.image.sc/tag/qupath

------

## License

This is just a template, you're free to use it however you like.
You can treat the contents of *this repository only* as being under [the Unlicense](https://unlicense.org) (except for the Gradle wrapper, which has its own license included).

If you use it to create a new QuPath extension, I'd strongly encourage you to select a suitable open-source license for the extension.

Note that *QuPath itself* is available under the GPL, so you do have to abide by those terms: see https://github.com/qupath/qupath for more.
