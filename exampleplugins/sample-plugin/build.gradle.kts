plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
    google()
}

dependencies {
    compileOnly(files("../../plugin-api/build/libs/plugin-api-jvm.jar"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

tasks.jar {
    archiveBaseName.set("sample-plugin")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
