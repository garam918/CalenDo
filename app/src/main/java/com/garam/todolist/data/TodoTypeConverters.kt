package com.garam.todolist.data

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class TodoTypeConverters {

    @TypeConverter
    fun fromString(value: String): MutableMap<String, TodoStatus>? {

        val mapType = object : TypeToken<MutableMap<String, TodoStatus>>() {}.type

        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromMap(map: MutableMap<String,TodoStatus>): String {
        val gson = Gson()

        return gson.toJson(map)
    }
}