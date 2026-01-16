plugins {
    id("java")
    id("app.ultradev.hytalegradle") version "1.5.0"
}

group = project.property("maven_group") as String
version = project.property("version") as String

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2")
}

hytale {
    allowOp.set(true)
    patchline.set(project.property("patchline") as String)
}