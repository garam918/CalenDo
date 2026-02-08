package com.garam.shared.notification

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun rememberPermissionHandler(onResult: (PermissionStatus) -> Unit): PermissionHandler {
    val context = LocalContext.current

    // Android 13(SDK 33) 이상을 위한 런타임 권한 런처
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            onResult(if (isGranted) PermissionStatus.GRANTED else PermissionStatus.DENIED)
        }
    )

    return remember(launcher) {
        object : PermissionHandler {

            override suspend fun checkPermissionStatus(): PermissionStatus {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Android 13 이상: 런타임 권한 체크

                    val isGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (isGranted) return PermissionStatus.GRANTED

                    // Android는 '거부'와 '아직 안 물어봄'을 완벽히 구분하기 까다롭지만,
                    // shouldShowRequestPermissionRationale 등을 활용할 수 있습니다.
                    // 여기서는 편의상 권한이 없으면 일단 NOT_DETERMINED 취급하여 팝업을 시도하거나,
                    // 이미 거부된 이력이 있으면 DENIED로 처리하는 로직을 추가할 수 있습니다.
                    // 간단한 구현을 위해 여기서는 권한이 없으면 NOT_DETERMINED로 리턴하고,
                    // UI에서 askPermission을 호출했을 때 시스템이 알아서 처리하게 합니다.
                    return PermissionStatus.NOT_DETERMINED

//                    ContextCompat.checkSelfPermission(
//                        context,
//                        Manifest.permission.POST_NOTIFICATIONS
//                    ) == PackageManager.PERMISSION_GRANTED
                } else {
                    // Android 12 이하: 설치 시점에 권한이 자동 부여됨
                    PermissionStatus.GRANTED
                }
            }

            override fun askPermission() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // 권한이 이미 있는지 체크
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        onResult(PermissionStatus.GRANTED) // 이미 권한 있음
                    } else {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS) // 권한 요청 팝업 띄우기
                    }
                } else {
                    // Android 12 이하는 별도 권한 요청 없이 알림 가능
                    onResult(PermissionStatus.GRANTED)
                }
            }

            override fun openAppSettings() {
                try {
                    // 1. 앱 알림 설정 화면으로 직행 시도 (Android 8.0+)
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        // Intent.FLAG_ACTIVITY_NEW_TASK가 필요할 수 있음 (Context가 Activity가 아닌 경우)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)

                } catch (e: Exception) {
                    // 3. 모든 게 실패하면 일반 앱 정보 화면으로 이동
                    openAppDetails(context)
                }
            }

            private fun openAppDetails(context: android.content.Context) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        }
    }
}