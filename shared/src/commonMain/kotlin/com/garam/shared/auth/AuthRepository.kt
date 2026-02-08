package com.garam.shared.auth

import com.garam.shared.data.source.local.LocalUserData

interface AuthRepository {

    suspend fun isExistAccount(uid: String) : Boolean

    suspend fun signInAnonymously(): LocalUserData?
    suspend fun signInWithGoogle(idToken: String, accessToken : String): LocalUserData?

    suspend fun linkInWithApple() : LocalUserData?

    suspend fun signInWithApple(): LocalUserData?
    suspend fun signOut()

    suspend fun reAuthenticate(idToken: String, accessToken : String) : Boolean

    suspend fun deleteAccount()
    fun currentUser(): LocalUserData?
}