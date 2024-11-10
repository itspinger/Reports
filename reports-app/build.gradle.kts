plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "raf.rs"
version = "1.0-SNAPSHOT"
application {

    mainClass.set("raf.rs.Reports.reports-app.TestKt")
}


repositories {
    mavenCentral()
    mavenLocal()
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { it.name.endsWith("jar") }.map { zipTree(it) })
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


