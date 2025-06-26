plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.googlDevToolsKSP)
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("androidx.room")
}


android {
    namespace = "com.garam.todolist"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.garam.todolist"
        minSdk = 30
        targetSdk = 35
        versionCode = 14
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures{
        viewBinding = true
        dataBinding = true

        buildConfig = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    configurations.implementation{
        exclude(group = "com.intellij", module = "annotations")
    }
}

room {
    schemaDirectory("$projectDir/schemas")
//    schemaDirectory.set(file("schemas"))
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.androidx)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.room)
    implementation(libs.room.runtime)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    ksp (libs.room.compiler)

    implementation(libs.androidx.cardview)


    implementation(libs.hilt)
    kapt(libs.hilt.compiler)

    implementation(libs.hilt.android.testing)
    testImplementation(libs.hilt.android.testing)
    testAnnotationProcessor(libs.hilt.android.compiler)
    kaptTest(libs.hilt.android.compiler)

    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.android.compiler)
    kapt (libs.dagger.hilt.compiler)

    implementation(libs.coroutine.core)
    implementation(libs.coroutine.android)

    implementation("com.kizitonwose.calendar:view:2.6.2")

    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(libs.viewpager)

    implementation(libs.code.gson)

    implementation(libs.google.services.auth)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}