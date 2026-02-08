plugins {
    kotlin("jvm")
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    application
}

dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
}

application {
    mainClass = "MainKt"
}
