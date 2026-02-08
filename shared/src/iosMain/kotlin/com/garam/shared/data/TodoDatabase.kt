@file:OptIn(ExperimentalForeignApi::class)

package com.garam.shared.data

import androidx.room.Room
import androidx.room.RoomDatabase
import com.garam.shared.data.source.local.TodoDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getDatabaseBuilder(): RoomDatabase.Builder<TodoDatabase> {
    val dbFilePath = documentDirectory() + "/todo.db"
    return Room.databaseBuilder<TodoDatabase>(
        name = dbFilePath,
//        factory = { TodoDatabase::class }
    )
}

private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}