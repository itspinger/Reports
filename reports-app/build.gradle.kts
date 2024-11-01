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
    implementation(project(":reports-csv"))
    implementation(project(":reports-excel"))
    implementation(project(":reports-pdf"))
    implementation(project(":reports-txt"))
    implementation("com.google.code.gson:gson:2.11.0")
    implementation(project(":reports-calculations"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}