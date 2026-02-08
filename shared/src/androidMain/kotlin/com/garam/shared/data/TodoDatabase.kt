package com.garam.shared.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.garam.shared.data.source.local.TodoDatabase


fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<TodoDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("todo.db")
    return Room.databaseBuilder<TodoDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}