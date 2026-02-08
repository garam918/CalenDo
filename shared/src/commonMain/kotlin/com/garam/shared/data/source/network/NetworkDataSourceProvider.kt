package com.garam.shared.data.source.network

import com.garam.shared.data.source.local.TodoDao

expect class NetworkDataSourceProvider() {
    fun get(todoDao: TodoDao) : NetworkDataSource
}