pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "inopay-android"

// Module :ui (Android library + Compose, com.inopay:inopay-android-ui) — 14 écrans
// canoniques du parcours investisseur. Voir ui/build.gradle.kts.
include(":ui")
