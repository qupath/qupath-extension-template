pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://maven.scijava.org/content/repositories/releases")
        }
    }
}

// TODO: Specify which version of QuPath the extension is targeting here
qupath {
    version = "0.6.0-SNAPSHOT"
}

// Apply QuPath Gradle settings plugin to handle configuration
plugins {
    id("io.github.qupath.qupath-extension-settings") version "0.2.1"
}
