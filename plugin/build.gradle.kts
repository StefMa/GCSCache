version = "0.0.1"
group = "guru.stefma.gcs.cache"

plugins {
    kotlin("jvm") version "1.2.21"
    id("java-gradle-plugin")
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.cloud:google-cloud-storage:1.12.0")
}