plugins {
    kotlin("jvm")
}

group = "raf.rs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":spi"))

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}