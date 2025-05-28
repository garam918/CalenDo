package com.garam.todolist.data.source.network

import com.garam.todolist.data.UserIdProvider
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserIdProvider @Inject constructor() : UserIdProvider {
    override val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("User is not logged in")
}