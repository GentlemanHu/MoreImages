import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.gentleman.hu"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                api("com.tinify:tinify:latest.release")
                implementation("com.sksamuel.scrimage:scrimage-core:4.0.37")
                implementation("com.sksamuel.scrimage:scrimage-webp:4.0.37")
                implementation("com.russhwolf:multiplatform-settings-no-arg:1.0.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.2")
            }
        }
        val jvmTest by getting
    }

}


compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            includeAllModules = true
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi)
            macOS {
                iconFile.set(project.file("src/jvmMain/resources/assets/image_trans_logo.icns"))
            }
            windows {
                iconFile.set(project.file("src/jvmMain/resources/assets/image_trans_logo.ico"))
            }
            packageName = "MoreImages"
            packageVersion = "1.0.0"
        }
    }
}
