package com.garam.todolist.data.source.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.garam.todolist.data.TodoTypeConverters


@Database(entities = [LocalTodo::class, LocalCategory::class, LocalGoal::class, LocalUserData::class]
    , version = 1, exportSchema = true)
@TypeConverters(TodoTypeConverters::class)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDao() : TodoDao
    abstract fun categoryDao() : CategoryDao
    abstract fun goalDao() : GoalDao

    abstract fun accountDao() : AccountDao
}