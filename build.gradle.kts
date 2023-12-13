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
    maven("https://oss.sonatype.org/content/repositories/snapshots")
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
                implementation("org.jetbrains.kotlin:kotlin-reflect:${extra["kotlin.version"]}")
                api ("com.badlogicgames:libimagequant-java:1.2-SNAPSHOT")
                // https://mvnrepository.com/artifact/net.coobird/thumbnailator
//                implementation("net.coobird:thumbnailator:0.4.20")
                // 不支持多选 https://github.com/Wavesonics/compose-multiplatform-file-picker
//                implementation("com.darkrockstudios:mpfilepicker:1.2.0")

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
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            macOS {
                iconFile.set(project.file("src/jvmMain/resources/assets/image_trans_logo.icns"))
            }
            windows {
                iconFile.set(project.file("src/jvmMain/resources/assets/image_trans_logo.ico"))
            }
            linux {
                iconFile.set(project.file("src/jvmMain/resources/assets/image_trans_logo.png"))
            }
            packageName = "MoreImages"
            packageVersion = "1.0.1"
        }
    }
}
