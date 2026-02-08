package com.garam.shared.auth

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.garam.todolist.Res
import com.garam.todolist.google_web_client_id
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.async
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import kotlin.coroutines.suspendCoroutine

private class AndroidGoogleAuthHandler(private val activity: ComponentActivity,
    private val clientId: String) : GoogleAuthHandler {
    override suspend fun signIn(): List<String?> {

            val credentialManager = CredentialManager.create(activity)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(clientId)
                .build()

            val credentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            return try {
                val result = credentialManager.getCredential(
                    request = credentialRequest,
                    context = activity
                )
                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    listOf(googleIdTokenCredential.idToken, "")
                } else {
                    listOf()
                }
            } catch (e: Exception) {
                // 로그인 취소 또는 오류 처리
                e.printStackTrace()
                listOf()
            }

    }
}

@Composable
actual fun rememberGoogleAuthHandler(): GoogleAuthHandler {
    val context = LocalContext.current
    // Context를 Activity로 변환합니다.
    val activity = context as? ComponentActivity
        ?: throw IllegalStateException("LocalContext is not a ComponentActivity.")

    val clientId = stringResource(Res.string.google_web_client_id)

    // remember를 사용하여 핸들러 인스턴스를 한 번만 생성합니다.
    return remember { AndroidGoogleAuthHandler(activity,clientId) }
}