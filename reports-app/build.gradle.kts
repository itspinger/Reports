plugins {
    kotlin("jvm")
    `java-library`
}

group = "raf.rs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":spi"))
    runtimeOnly(project(":reports-csv"))
    runtimeOnly(project(":reports-excel"))
    runtimeOnly(project(":reports-pdf"))
    runtimeOnly(project(":reports-txt"))
    implementation("com.google.code.gson:gson:2.11.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}