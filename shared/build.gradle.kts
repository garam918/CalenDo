@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    alias(libs.plugins.googlDevToolsKSP)
    alias(libs.plugins.androidx.room)

    alias(libs.plugins.serializationPlugins)

    alias(libs.plugins.kotlinCocoapods)

//    id("com.google.gms.google-services")

}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets

//    jvm("desktop")

    iosX64()
    iosArm64()
    iosSimulatorArm64()

//    val xcframeworkName = "Shared"
//    val xcf = XCFramework(xcframeworkName)
//
//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64(),
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "Shared"
//            isStatic = true
//
//        }
//
//        iosTarget.compilations.getByName("main") {
//            cinterops.create("FirebaseFirestoreInterop") {
//                defFile("src/nativeInterop/cinterop/FirebaseFirestore.def")
//                packageName = "cocoapods.FirebaseFirestoreInterop"
//
//            }
//        }
//    }


    cocoapods {
        version = "1.0"
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"
        podfile = project.file("../iosApp/Podfile")


        name = "Shared"

        ios.deploymentTarget = "26.2"


        pod("FirebaseAuth") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("FirebaseFirestore") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("GoogleSignIn") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }


        framework {
            baseName = "Shared"
            isStatic = false
            transitiveExport = false // This is default.

            binaryOption("bundleId", "org.example.Shared")
            linkerOpts.add("-lsqlite3")
//            linkerOpts("-framework", "Network")


//            export(project(":shared"))
        }
    }


    androidLibrary {
        namespace = "com.garam.todolist"
        compileSdk = 36
        minSdk = 30


        androidResources.enable = true



        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                implementation(libs.navigation.compose)

                implementation(libs.kizitonwose.compose.multiplatform)
                implementation(libs.room.runtime)
                implementation(libs.androidx.sqlite.bundled)

                implementation(libs.kotlinx.serialization.json)

                api(libs.koin.core)
                api(libs.androidx.lifecycle.viewmodel)

                implementation(libs.koin.compose.viewmodel)

                // date, time picker
                implementation(libs.compose.date.time.picker)

                implementation(libs.kotlinx.datetime)


//                implementation(libs.gitlive.firebase.auth)
                implementation(libs.gitlive.firebase.firestore)

                implementation(libs.firebase.common)

//                implementation(libs.compose.resources)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)

                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)
            }
        }

        androidMain {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation(compose.uiTooling)

                implementation(libs.androidx.room.sqlite.wrapper)

                implementation(libs.koin.android)
                implementation(libs.googleid)

                val bom = project.dependencies.platform("com.google.firebase:firebase-bom:33.1.0")
                implementation(bom)
                implementation(libs.google.firebase.auth)
                implementation(libs.google.services.auth)
                implementation(libs.firebase.firestore)
            }
        }


        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.junit)
            }
        }

        iosMain {
            dependencies {

//                implementation(libs.gitlive.firebase.auth)
            }
        }
//        val desktopMain by getting {
//            dependencies {
//                implementation(compose.desktop.currentOs)
//
//            }
//
//        }
    }


}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
}

//compose.desktop {
//
//    application {
//        mainClass = "MainKt"
//    }
//
//}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.garam.todolist"
    generateResClass = auto
}

tasks.register("syncAndRun", Exec::class) {
    dependsOn(tasks.getByName("syncFramework"))
    workingDir = rootDir.resolve("iosApp")
    commandLine("sh", "-c", "xcodebuild -showsdks | grep iphoneos && open ${project.name}.xcworkspace")
}

tasks.register("generateXcodeProject") {
    dependsOn(tasks.getByName("podInstall")) // Cocoapods를 사용하는 경우
}
