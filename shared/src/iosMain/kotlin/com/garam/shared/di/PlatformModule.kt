package com.garam.shared.di

import com.garam.shared.data.getDatabaseBuilder
import com.garam.shared.data.source.TodoRepository
import com.garam.shared.data.source.local.TodoDatabase
import com.garam.shared.data.source.local.getTodoDatabase
import org.koin.core.module.Module
import org.koin.dsl.module


actual fun platformModule(): Module = module {
    single<TodoDatabase> {
        val builder = getDatabaseBuilder()
        getTodoDatabase(builder)
    }
}
