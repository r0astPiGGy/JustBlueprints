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
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
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
        }
    }
}

dependencies {
    commonMainImplementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.4.3")
    commonMainImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    commonMainImplementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
}