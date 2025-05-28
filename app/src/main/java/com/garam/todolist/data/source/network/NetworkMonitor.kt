package com.garam.todolist.data.source.network

interface NetworkMonitor {
    fun isConnected(): Boolean
}