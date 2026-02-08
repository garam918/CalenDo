package com.garam.shared.data.source.network

import com.garam.shared.data.source.local.TodoDao

actual class NetworkDataSourceProvider {
    actual fun get(todoDao: TodoDao) : NetworkDataSource = NetworkDataSourceImpl(todoDao)
}