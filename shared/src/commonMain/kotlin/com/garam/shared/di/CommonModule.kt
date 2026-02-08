package com.garam.shared.di

import com.garam.shared.auth.AuthRepository
import com.garam.shared.auth.AuthRepositoryProvider
import com.garam.shared.data.source.DefaultSettingRepository
import com.garam.shared.data.source.DefaultTodoRepository
import com.garam.shared.data.source.SettingRepository
import com.garam.shared.data.source.TodoRepository
import com.garam.shared.data.source.local.AccountDao
import com.garam.shared.data.source.local.CategoryDao
import com.garam.shared.data.source.local.GoalDao
import com.garam.shared.data.source.local.TodoDao
import com.garam.shared.data.source.local.TodoDatabase
import com.garam.shared.data.source.network.NetworkDataSource
import com.garam.shared.data.source.network.NetworkDataSourceProvider
import com.garam.shared.ui.login.LoginViewModel
import com.garam.shared.ui.setting.SettingViewModel
import com.garam.shared.ui.todolist.TodoViewModel
import com.garam.shared.widget.WidgetUpdate
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun commonModule(): Module = module {
    single<TodoDao> { get<TodoDatabase>().todoDao() }
    single<CategoryDao> { get<TodoDatabase>().categoryDao() }
    single<GoalDao> { get<TodoDatabase>().goalDao() }
    single<AccountDao> { get<TodoDatabase>().accountDao() }
    single<AuthRepository> { get<AuthRepositoryProvider>().get() }
    single<NetworkDataSource> { get<NetworkDataSourceProvider>().get(todoDao = get<TodoDatabase>().todoDao()) }
    single<TodoRepository> { get<DefaultTodoRepository>() }
    single<SettingRepository> { get<DefaultSettingRepository>() }
    singleOf(::AuthRepositoryProvider)
    factory { TodoViewModel(get(), get()) }
    factory { LoginViewModel(get(), get(), get(), get(), get()) }
    factory { SettingViewModel( get(), get(), get(), get(), get() ) }
    singleOf(::NetworkDataSourceProvider)
    singleOf(::DefaultTodoRepository)
    singleOf(::DefaultSettingRepository)
}