package com.garam.shared.data

import platform.Foundation.NSUserDefaults

actual object AppPreferences {
    private val defaults = NSUserDefaults.standardUserDefaults()

    actual fun setString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    actual fun getString(key: String, defaultValue: String?): String? {
        return defaults.stringForKey(key) ?: defaultValue
    }

    actual fun setBoolean(key: String, value: Boolean) {
        defaults.setBool(value, forKey = key)
    }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (defaults.objectForKey(key) != null) {
            defaults.boolForKey(key)
        } else defaultValue
    }

    actual fun clear() {
        val dict = defaults.dictionaryRepresentation()
        dict.keys.forEach { key ->
            defaults.removeObjectForKey(key as String)
        }
    }
}