import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.8.0"
    id("org.jetbrains.compose")
}

group = "com.rodev"
version = "1.0-SNAPSHOT"

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
            packageVersion = "1.0.0"
            buildTypes.release.proguard {
                configurationFiles.from(project.file("proguard-rules.pro"))
            }

            macOS { iconFile.set(project.file("logo.icns")) }
            windows { iconFile.set(project.file("logo.ico")) }
            linux { iconFile.set(project.file("logo.png")) }
        }
    }
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