plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "raf.rs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    runtimeOnly(project(":reports-csv"))
    runtimeOnly(project(":reports-excel"))
    runtimeOnly(project(":reports-pdf"))
    runtimeOnly(project(":reports-txt"))

    implementation(project(":spi"))
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.mysql:mysql-connector-j:8.3.0")
}


application {
    mainClass.set("raf.rs.reports.testapp.TestKt")
}

tasks.shadowJar {
    archiveClassifier.set("all")
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    mergeServiceFiles()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

kotlin {
    jvmToolchain(21)
}


