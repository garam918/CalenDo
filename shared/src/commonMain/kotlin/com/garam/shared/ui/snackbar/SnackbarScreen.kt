package com.garam.shared.ui.snackbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
fun SnackbarScreen(snackbarHostState: SnackbarHostState) {

    // 1. 스낵바의 상태를 저장하고 기억합니다.
    val snackbarHostState = remember { snackbarHostState }

    // 2. 스낵바를 띄우기 위한 코루틴 스코프를 생성합니다.
    val scope = rememberCoroutineScope()

    // 3. Scaffold를 사용하여 화면의 기본 레이아웃을 구성합니다.
    Scaffold(
        // 4. snackbarHost 슬롯에 SnackbarHost를 연결합니다.
        // 이 호스트가 snackbarHostState의 상태를 관찰하고 스낵바를 표시합니다.
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding -> // Scaffold가 화면 요소가 가려지지 않도록 패딩 값을 제공합니다.

        // 5. 화면 중앙에 버튼을 배치하기 위한 Box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // 시스템 UI(상단바 등)와 겹치지 않도록 패딩 적용
            contentAlignment = Alignment.Center
        ) {
            // 6. 스낵바를 띄우는 트리거 버튼
            Button(onClick = {
                // 7. 버튼 클릭 시, 코루틴을 실행합니다.
                scope.launch {
                    // 8. 스낵바를 보여주는 suspend 함수를 호출합니다.
                    snackbarHostState.showSnackbar(
                        message = "안녕하세요! 스낵바입니다. 👋"
                    )
                }
            }) {
                Text("스낵바 보여주기")
            }
        }
    }
}