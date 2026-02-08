package com.garam.shared.auth

import com.garam.shared.data.source.local.LocalUserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {

    override suspend fun isExistAccount(uid: String): Boolean {

        return Firebase.firestore.collection("users").document(uid).get().await().exists()

    }

    override suspend fun signInAnonymously(): LocalUserData? {

        val user = Firebase.auth.signInAnonymously().await().user
        return LocalUserData(uid = user?.uid.toString(), email = "Guest", loginType = "anonymous")
    }

    override suspend fun signInWithGoogle(idToken: String, accessToken : String): LocalUserData? {

        val googleCredential = GoogleAuthProvider.getCredential(idToken, null)


        println("google credential ${googleCredential.provider}")
        // 게스트 로그인 연결 기능 구현 필요
        if(Firebase.auth.currentUser != null && Firebase.auth.currentUser?.isAnonymous == true) {

            val task = Firebase.auth.currentUser?.linkWithCredential(googleCredential)

            var user : FirebaseUser?

            if(task?.isSuccessful == true) {
                user = Firebase.auth.currentUser?.linkWithCredential(googleCredential)?.await()?.user
            }
            else {
                user = Firebase.auth.signInWithCredential(googleCredential).await().user
            }

            println("google user $user")


            val email = user?.email
            val uid = user?.uid.toString()
            val loginType = "google"

            return LocalUserData(uid = uid, email = email, loginType = loginType)
        }
        else {

            val user = Firebase.auth.signInWithCredential(googleCredential).await().user

            val email = user?.email
            val uid = user?.uid.toString()
            val loginType = "google"

            return LocalUserData(uid = uid, email = email, loginType = loginType)
        }
    }

    override suspend fun reAuthenticate(idToken: String, accessToken: String): Boolean {

        val googleCredential = GoogleAuthProvider.getCredential(idToken, null)
        return Firebase.auth.signInWithCredential(googleCredential).await().user != null
    }

    override suspend fun linkInWithApple(): LocalUserData? = null

    override suspend fun signInWithApple(): LocalUserData? = null

    override suspend fun deleteAccount() {

        val currentUser = Firebase.auth.currentUser

        Firebase.firestore.collection("users").document(currentUser?.uid.toString())
            .delete().await()

        currentUser?.delete()?.await()


    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    override fun currentUser(): LocalUserData? {
        val user = Firebase.auth.currentUser

        val loginType = if(user?.isAnonymous == true) "anonymous"
            else when(user?.providerData[1]?.providerId) {
            "google.com" -> "google"
            "apple.com" -> "apple"
            else -> ""
        }

        return if(user == null) null else LocalUserData(uid = user.uid, email = user.email, loginType = loginType)
    }
}