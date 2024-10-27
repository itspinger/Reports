plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "reports"

include(
    "spi",
    "reports-csv",
    "reports-txt",
    "reports-excel",
    "reports-pdf"
)
include("reports-app")
