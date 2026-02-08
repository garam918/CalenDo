package com.garam.shared

import android.app.Application
import com.garam.shared.data.AppPreferences
import com.garam.shared.di.initKoin
import com.garam.shared.util.AppInfo
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

//        CoroutineScope(Dispatchers.Main).launch {
//            initFirebaseAuth()
//        }
        AppInfo.init(this)
        AppPreferences.init(this)

        initKoin(
            appDeclaration = { androidContext(this@MainApplication) },
        )
    }
}