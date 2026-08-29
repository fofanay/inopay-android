plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.serialization") version "1.9.22"
    `java-library`
    `maven-publish`
}

group = "com.inopay"
version = "0.1.0-alpha.2"

repositories {
    mavenCentral()
}

dependencies {
    // api: ces lib apparaissent dans la signature publique de l'API.
    api("com.squareup.okhttp3:okhttp:4.12.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    // serialization: utilisée en interne via @Serializable, pas exposée.
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.inopay"
            artifactId = "inopay-android"
            version = "0.1.0-alpha.2"
            from(components["java"])
        }
    }
}
