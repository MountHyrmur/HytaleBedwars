plugins {
    kotlin("jvm") version "2.3.0"
    id("app.ultradev.hytalegradle") version "2.0.1"
}

group = project.property("maven_group") as String
version = project.property("version") as String

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

hytale {
    allowOp.set(true)
    patchline.set(project.property("patchline") as String)
    includeLocalMods.set(true)

    manifest {
        version.set(project.version.toString())
        author("SzczurekYT", url = "https://github.com/SzczurekYT/")
        dependencies = mapOf("Kotale:Kotlin" to "*")
    }
}