import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    kotlin("plugin.compose") version "2.2.20"
    id("org.jetbrains.compose") version "1.7.3"
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
    
    // Compose
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.ui)
}

tasks.jar {
    archiveBaseName.set("sample-plugin")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Copy>("copyDependencies") {
    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("libs/lib"))
}

tasks.register<Zip>("packagePlugin") {
    group = "build"
    description = "Packages the plugin into a .micyou-plugin.zip file"

    dependsOn(tasks.jar)
    dependsOn("copyDependencies")

    archiveFileName.set("sample-plugin.micyou-plugin.zip")
    destinationDirectory.set(layout.buildDirectory.dir("distributions"))

    // 添加 plugin.json
    from("src/main/resources/plugin.json")

    // 添加编译后的类文件
    from(tasks.jar.get().outputs.files) {
        into("classes")
    }

    // 添加依赖库
    from(layout.buildDirectory.dir("libs/lib")) {
        into("lib")
    }

    // 添加图标（如果存在）
    from("src/main/resources") {
        include("icon.png")
    }
}
