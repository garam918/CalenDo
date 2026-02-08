package com.garam.shared

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
//import com.garam.todolist.App // ✅ ":composeApp" 모듈에 있는 공통 UI를 가져옵니다.

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "To-Do List") {
        // 공통 모듈에 정의해 둔 App() 컴포저블을 여기서 실행합니다.
        App()
    }
}