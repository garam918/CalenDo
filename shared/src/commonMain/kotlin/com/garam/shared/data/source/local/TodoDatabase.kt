package com.garam.shared.data.source.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.garam.shared.data.TodoTypeConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [LocalTodo::class, LocalCategory::class, LocalGoal::class, LocalUserData::class]
    , version = 1, exportSchema = true)
@ConstructedBy(AppDatabaseConstructor::class)
@TypeConverters(TodoTypeConverters::class)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDao() : TodoDao
    abstract fun categoryDao() : CategoryDao
    abstract fun goalDao() : GoalDao

    abstract fun accountDao() : AccountDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<TodoDatabase> {
    override fun initialize(): TodoDatabase
}

fun getTodoDatabase(
    builder: RoomDatabase.Builder<TodoDatabase>
): TodoDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}