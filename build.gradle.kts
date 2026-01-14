import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    id("java")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.3"
}

val mod_version: String by project
val maven_group: String by project
val includes_pack: String by project
val patchline: String by project
val load_user_mods: String by project
// If you are on Windows uncomment this
//val hytaleHome = "${System.getProperty("user.home")}/AppData/Roaming/Hytale"
val hytaleHome = "${System.getProperty("user.home")}/.var/app/com.hypixel.HytaleLauncher/data/Hytale"
val serverRunDir = file("$projectDir/run")

group = maven_group
version = mod_version

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    compileOnly("org.jetbrains:annotations:26.0.2")

    implementation(files("$hytaleHome/install/$patchline/package/game/latest/Server/HytaleServer.jar"))
}

tasks.test {
    useJUnitPlatform()
}

idea.project.settings.runConfigurations {
    create<org.jetbrains.gradle.ext.Application>("HytaleServer") {
        mainClass = "com.hypixel.hytale.Main"
        moduleName = project.idea.module.name + ".main"
        programParameters = "--allow-op --assets=$hytaleHome/install/$patchline/package/game/latest/Assets.zip"
        if (includes_pack.toBoolean()) {
            programParameters += " --mods=${file("src/main/").absolutePath}"

        }
        if (load_user_mods.toBoolean()) {
            programParameters += " --mods=$hytaleHome/UserData/Mods"
        }
        workingDirectory = "run"
    }
}
