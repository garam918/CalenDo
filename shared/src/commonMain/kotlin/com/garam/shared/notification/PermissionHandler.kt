package com.garam.shared.notification

import androidx.compose.runtime.Composable

interface PermissionHandler {

    suspend fun checkPermissionStatus(): PermissionStatus
    fun askPermission()

    fun openAppSettings()
}

// 2. 플랫폼별로 PermissionHandler를 만들어주는 Composable 함수
// onResult: 사용자가 권한을 허용했는지(true) 거부했는지(false) 결과를 돌려받는 콜백
@Composable
expect fun rememberPermissionHandler(onResult: (PermissionStatus) -> Unit): PermissionHandler

enum class PermissionStatus {
    GRANTED,        // 허용됨
    DENIED,         // 거부됨 (설정 화면으로 가야 함)
    NOT_DETERMINED  // 아직 결정 안 됨 (시스템 팝업 띄워야 함)
}