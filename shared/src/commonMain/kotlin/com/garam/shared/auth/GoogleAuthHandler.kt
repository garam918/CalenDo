package com.garam.shared.auth

import androidx.compose.runtime.Composable

interface GoogleAuthHandler {
    suspend fun signIn(): List<String?>
}
@Composable
expect fun rememberGoogleAuthHandler(): GoogleAuthHandler