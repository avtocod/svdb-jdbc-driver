plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "svdb-jdbc-driver"
include("model","client","jdbc","test")
