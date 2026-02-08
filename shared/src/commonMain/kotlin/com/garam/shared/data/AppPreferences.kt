package com.garam.shared.data

expect object AppPreferences {
    fun setString(key: String, value: String)
    fun getString(key: String, defaultValue: String? = null): String?
    fun setBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    fun clear()
}