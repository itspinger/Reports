plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "1.8.10"
    `java-library`

    `maven-publish`
}

group = "raf.rs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(project(":reports-calculations"))
}

tasks.test {
    useJUnitPlatform()
}
tasks.javadoc {
    dependsOn(tasks.dokkaJavadoc)
    doLast {
        println("Javadoc generated at: ${tasks.dokkaJavadoc.get().outputDirectory}")
    }
}
tasks.dokkaJavadoc {

    outputDirectory.set(file("build/dokka/javadoc"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

kotlin {
    jvmToolchain(21)
}