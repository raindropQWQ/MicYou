plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
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
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<Copy>("createPluginPackage") {
    dependsOn("jar")
    
    val outputDir = layout.buildDirectory.dir("plugin-package")
    
    from(layout.buildDirectory.dir("libs")) {
        include("*.jar")
        rename(".*", "plugin.jar")
    }
    from(layout.projectDirectory.dir("src/main/resources")) {
        include("plugin.json")
    }
    
    into(outputDir)
    
    doLast {
        val packageDir = outputDir.get().asFile
        val zipFile = layout.buildDirectory.file("sample-plugin.micyou-plugin.zip").get().asFile
        
        exec {
            workingDir = packageDir
            if (System.getProperty("os.name").lowercase().contains("windows")) {
                commandLine("powershell", "-Command", "Compress-Archive -Path * -DestinationPath $zipFile -Force")
            } else {
                commandLine("zip", "-r", zipFile.absolutePath, ".")
            }
        }
        
        println("Plugin package created: ${zipFile.absolutePath}")
    }
}
