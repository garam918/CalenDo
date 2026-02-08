// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.googlDevToolsKSP) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.3" apply false
//    id("androidx.room") version "2.7.2" apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
//    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false

    alias(libs.plugins.kotlinCocoapods) apply false
}