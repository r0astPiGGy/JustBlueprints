import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.com.google.common.io.Files
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.8.0"
    id("org.jetbrains.compose")
}

group = "com.rodev"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

val ktorVersion = "2.3.3"

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("com.darkrockstudios:mpfilepicker:1.2.0")
                implementation(project(":node-ui"))
                implementation(project(":action-generator"))
                implementation(project(":common"))
                implementation(project(":compiler"))
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "com.rodev.jbpkmp.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "JustBlueprints"
            packageVersion = version.toString()
            buildTypes.release.proguard {
                configurationFiles.from(project.file("proguard-rules.pro"))
            }

            macOS { iconFile.set(project.file("JustBlueprintsLogo-Rounded.icns")) }
            windows { iconFile.set(project.file("JustBlueprintsLogo-Rounded.ico")) }
            linux { iconFile.set(project.file("JustBlueprintsLogo-Rounded.png")) }
        }
    }
}

tasks.register("buildMacosRelease") {
    dependsOn("packageReleaseDmg")
    doLast {
        Files.copy(
            findReleaseOutput("dmg"),
            File("justblueprints-macos.dmg")
        )
    }
}

tasks.register("buildLinuxRelease") {
    dependsOn("packageReleaseDeb")
    doLast {
        Files.copy(
            findReleaseOutput("deb"),
            File("justblueprints-linux.deb")
        )
    }
}

tasks.register("buildWindowsRelease") {
    dependsOn("packageReleaseMsi")
    doLast {
        Files.copy(
            findReleaseOutput("msi"),
            File("justblueprints-windows.msi")
        )
    }
}

fun findReleaseOutput(suffix: String): File {
    val path = "./build/compose/binaries/main-release/$suffix/".replace("/", File.separator)

    return findReleaseOutput(path) {
        it.name.endsWith(".$suffix")
    }
}

fun findReleaseOutput(path: String, predicate: (File) -> Boolean): File {
    var file: File? = null
    File(path).listFiles()?.forEach {
        if (predicate(it)) {
            file = it
            return@forEach
        }
    }
    return requireNotNull(file) { "Output file not found" }
}

dependencies {
    commonMainImplementation("org.slf4j:slf4j-api:1.7.25")
    commonMainImplementation("io.ktor:ktor-client-core:$ktorVersion")
    commonMainImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    commonMainImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    commonMainImplementation("io.ktor:ktor-client-cio:$ktorVersion")
    commonMainImplementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.4.3")
    commonMainImplementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
}