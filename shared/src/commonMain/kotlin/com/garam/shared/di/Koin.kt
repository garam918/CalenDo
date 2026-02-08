package com.garam.shared.di

import com.garam.shared.data.source.local.TodoDao
import com.garam.shared.platform
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            commonModule() + platformModule()
        )
    }
}

fun initKoinIos() = initKoin(appDeclaration = {})