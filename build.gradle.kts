plugins {
    // Main gradle plugin for building a Java library
    id("java-library")
    // Support writing the extension in Groovy (remove this if you don"t want to)
    id("groovy")
    // To create a shadow/fat jar that bundle up all dependencies
    id("com.gradleup.shadow") version "8.3.5"
    // Include this plugin to avoid downloading JavaCPP dependencies for all platforms
    id("org.bytedeco.gradle-javacpp-platform")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

// TODO: Change the module name
val moduleName = "io.github.qupath.extension.template"

// TODO: Define the extension name & version, and provide a short description
base {
    archivesName = rootProject.name
    version = "0.0.1-SNAPSHOT"
    description = "A simple QuPath extension template"
}

// TODO: Specify the QuPath version, compatible with the extension.
// The default 'gradle.ext.qupathVersion' reads this from settings.gradle.kts.
val qupathVersion = gradle.extra["qupath.app.version"]

// Get the Java version from QuPath's version catalog
val qupathJavaVersion = libs.versions.jdk.get()

javafx {
    version = qupathJavaVersion
    modules = listOf(
        "javafx.controls",
        "javafx.fxml",
        "javafx.web"
    )
}

/**
 * Define dependencies.
 * - Using 'shadow' indicates that they are already part of QuPath, so you don't need
 *   to include them in your extension. If creating a single 'shadow jar' containing your
 *   extension and all dependencies, these won't be added.
 * - Using 'implementation' indicates that you need the dependency for the extension to work,
 *   and it isn't part of QuPath already. If you are creating a single 'shadow jar', the
 *   dependency should be bundled up in the extension.
 * - Using 'testImplementation' indicates that the dependency is only needed for testing,
 *   but shouldn't be bundled up for use in the extension.
 */
dependencies {

    // Main QuPath user interface jar.
    // Automatically includes other QuPath jars as subdependencies.
    shadow("io.github.qupath:qupath-gui-fx:${qupathVersion}")

    // For logging - the version comes from QuPath's version catalog at
    // https://github.com/qupath/qupath/blob/main/gradle/libs.versions.toml
    // See https://docs.gradle.org/current/userguide/platforms.html
    shadow(libs.slf4j)

    // For JavaFX
    shadow(libs.qupath.fxtras)

    // If you aren't using Groovy, this can be removed
    shadow(libs.bundles.groovy)

    testImplementation("io.github.qupath:qupath-gui-fx:${qupathVersion}")
    testImplementation(libs.junit)
}

/*
 * Manifest info
 */
tasks.withType<Jar> {
    manifest {
        attributes(mapOf("Implementation-Title" to project.name,
                "Implementation-Version" to archiveVersion,
                "Automatic-Module-Name" to moduleName))
    }
}

/**
 * Copy necessary attributes, see
 * - https://github.com/qupath/qupath-extension-template/issues/9
 */
configurations.shadow {
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java,
            Usage.JAVA_RUNTIME))
        attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE,
            objects.named(OperatingSystemFamily::class.java, "org.gradle.native.operatingSystem"))
        attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE,
            objects.named(MachineArchitecture::class.java, "org.gradle.native.architecture"))
    }
}


/*
 * Copy the LICENSE file into the jar... if we have one (we should!)
 */
tasks.processResources {
  from("${projectDir}/LICENSE") {
    into("licenses/")
  }
}

/*
 * Define extra 'copyDependencies' task to copy dependencies into the build directory.
 */
tasks.register<Copy>("copyDependencies") {
    description = "Copy dependencies into the build directory for use elsewhere"
    group = "QuPath"

    from(configurations.default)
    into("build/libs")
}

/*
 * Ensure Java 17 compatibility, and include sources and javadocs when building.
 */
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(qupathJavaVersion)
    }
    withSourcesJar()
    withJavadocJar()
}

/*
 * Create javadocs for all modules/packages in one place.
 * Use -PstrictJavadoc=true to fail on error with doclint (which is rather strict).
 */
tasks.withType<Javadoc> {
	options.encoding = "UTF-8"
	val strictJavadoc = providers.gradleProperty("strictJavadoc").getOrElse("false")
	if ("true" == strictJavadoc) {
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
	}
}

/*
 * Specify that the encoding should be UTF-8 for source files
 */
tasks.compileJava {
	options.encoding = "UTF-8"
}

/*
 * Avoid 'Entry .gitkeep is a duplicate but no duplicate handling strategy has been set.'
 * when using withSourcesJar()
 */
tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

/*
 * Support tests with JUnit.
 */
tasks.test {
    useJUnitPlatform()
}

// Looks redundant to include this here and in settings.gradle.kts,
// but helps overcome some gradle trouble when including this as a subproject
// within QuPath itself (which is useful during development).
repositories {
    // Add this if you need access to dependencies only installed locally
    //  mavenLocal()

    mavenCentral()

    // Add scijava - which is where QuPath's jars are hosted
    maven {
        url = uri("https://maven.scijava.org/content/repositories/releases")
    }

    maven {
        url = uri("https://maven.scijava.org/content/repositories/snapshots")
    }

}
