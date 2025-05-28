package com.garam.todolist.util

import android.app.Activity
import android.content.Context
import android.content.Intent

object AppRestarter {

    fun restartApp(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val mainIntent = intent?.apply {
            addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(mainIntent)
        if (context is Activity) {
            context.finish()
        }

        // Optional: 강제로 프로세스 종료
        Runtime.getRuntime().exit(0)
    }

}