package com.garam.todolist

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.garam.shared.AppContext
import com.garam.shared.di.initKoin
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext

@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin(
            appDeclaration = { androidContext(this@MainApplication) },
        )

        val channel = NotificationChannel(
            "todo_channel_id",
            "일정 알림",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        AppContext.setUp(this)

        FirebaseApp.initializeApp(this)
//        Firebase.crashlytics.setUserId(FirebaseAuth.getInstance().currentUser?.uid.toString())

    }
}