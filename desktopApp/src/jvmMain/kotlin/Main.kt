package com.garam.shared

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.material.Text
import shared.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Compose Desktop App") {
        Text("Hello from Desktop!")
    }
}
