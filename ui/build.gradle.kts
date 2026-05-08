// inopay-android-ui — module Android Compose pour le parcours investisseur Inopay.
// Dépend du module racine (:) qui ship le client API pure-Kotlin.

plugins {
    id("com.android.library") version "8.2.2"
    kotlin("android") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    `maven-publish`
}

group = "com.inopay"
version = "0.2.0-alpha.1"

android {
    namespace = "com.inopay.ui"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"  // aligné Kotlin 1.9.22
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    // Le module racine (API client pure JVM Kotlin)
    api(project(":"))

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.02.02")
    api(composeBom)
    api("androidx.compose.ui:ui")
    api("androidx.compose.material3:material3")
    api("androidx.compose.material:material-icons-extended")
    api("androidx.compose.ui:ui-tooling-preview")

    // ViewModel / lifecycle
    api("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    api("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Coroutines
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Serialization (déjà dans le module racine, on ré-utilise)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Tooling — debug only
    debugImplementation("androidx.compose.ui:ui-tooling")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.inopay"
            artifactId = "inopay-android-ui"
            version = "0.2.0-alpha.1"
            // Le composant `release` est créé par AGP après application du plugin
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
