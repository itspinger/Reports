plugins {
    kotlin("jvm") version "2.0.0"
}

group = "raf.rs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}