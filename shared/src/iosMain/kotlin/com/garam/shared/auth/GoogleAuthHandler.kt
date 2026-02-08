package com.garam.shared.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cocoapods.GoogleSignIn.GIDConfiguration
import platform.UIKit.UIApplication
import cocoapods.GoogleSignIn.GIDSignIn
import com.garam.todolist.Res
import com.garam.todolist.google_ios_oauth_client_id
import com.garam.todolist.google_web_client_id
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource


private class IosGoogleAuthHandler(private val clientId: String) : GoogleAuthHandler {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun signIn(): List<String?> = withContext(Dispatchers.Main) {

        suspendCancellableCoroutine { continuation ->
            val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
            if (rootVC == null) {
                continuation.resume(listOf(), onCancellation = {})
                return@suspendCancellableCoroutine
            }


            val config = GIDConfiguration(
                clientID = clientId
            )
            GIDSignIn.sharedInstance.configuration = (config)
            GIDSignIn.sharedInstance.signInWithPresentingViewController(rootVC) { result, error ->
                if (error != null) {
                    println("❌ Google Sign-In error: ${error.localizedDescription}")
                    continuation.resume(listOf(), onCancellation = {})
                } else {
                    val token = result?.user?.idToken?.tokenString
                    val accessToken = result?.user?.accessToken?.tokenString
                    println("✅ Google ID Token: $token")
                    continuation.resume(listOf(token, accessToken), onCancellation = {})
                }
            }
        }
    }
}

@Composable
actual fun rememberGoogleAuthHandler(): GoogleAuthHandler {

    val clientId = stringResource(Res.string.google_ios_oauth_client_id)


    return remember { IosGoogleAuthHandler(clientId) }
}