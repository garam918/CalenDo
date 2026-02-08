package com.garam.shared.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UserNotifications.*
import kotlin.coroutines.resume

@Composable
actual fun rememberPermissionHandler(onResult: (PermissionStatus) -> Unit): PermissionHandler {
    // iOS는 Composable의 Context가 필요 없으므로 바로 객체 반환
    return remember {
        object : PermissionHandler {

            override suspend fun checkPermissionStatus(): PermissionStatus =
                suspendCancellableCoroutine { continuation ->
                    val center = UNUserNotificationCenter.currentNotificationCenter()

                    center.getNotificationSettingsWithCompletionHandler { settings ->
//                        val isGranted = settings?.authorizationStatus == UNAuthorizationStatusAuthorized
//                        continuation.resume(isGranted)

                        val status = when (settings?.authorizationStatus) {
                            UNAuthorizationStatusAuthorized,
                            UNAuthorizationStatusProvisional,
                            UNAuthorizationStatusEphemeral -> PermissionStatus.GRANTED

                            UNAuthorizationStatusDenied -> PermissionStatus.DENIED

                            // NotDetermined 상태일 때가 중요합니다.
                            UNAuthorizationStatusNotDetermined -> PermissionStatus.NOT_DETERMINED

                            else -> PermissionStatus.NOT_DETERMINED
                        }
                        continuation.resume(status)
                    }
                }

            override fun askPermission() {
                val center = UNUserNotificationCenter.currentNotificationCenter()
                val options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge

                center.requestAuthorizationWithOptions(options) { granted, error ->
//                    if (error != null) {

                        val status = if (granted) PermissionStatus.GRANTED else PermissionStatus.DENIED
                        onResult(status)
//                    }
//                    else {
//                        // iOS 콜백은 백그라운드 스레드에서 올 수 있으므로 주의 필요하지만,
//                        // 단순 Boolean 전달은 괜찮습니다. UI 업데이트 시에는 Dispatchers.Main이 필요할 수 있음.
//                        onResult(granted)
//                    }
                }
            }

            override fun openAppSettings() {
                val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
                if (settingsUrl != null && UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
                    UIApplication.sharedApplication.openURL(settingsUrl, mapOf<Any?, Any?>(), null)
                }
            }
        }
    }
}