package com.garam.shared.util


import platform.Foundation.NSBundle

actual object AppInfo {
    actual val appVersion: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "Unknown"

}
