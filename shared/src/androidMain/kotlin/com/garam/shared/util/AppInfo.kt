package com.garam.shared.util

import android.content.Context

actual object AppInfo {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    actual val appVersion: String
        get() {
            val pInfo = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
            return pInfo.versionName ?: "Unknown"
        }
}