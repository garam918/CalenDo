package com.garam.shared.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

actual object AppPreferences {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {

        prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    }

    actual fun setString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    actual fun getString(key: String, defaultValue: String?): String? {
        return prefs.getString(key, defaultValue)
    }

    actual fun setBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    actual fun clear() {
        prefs.edit { clear() }
    }
}