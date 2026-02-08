package com.garam.shared.data

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class TodoTypeConverters {

    @TypeConverter
    fun fromString(value: String): MutableMap<String, TodoStatus>? {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromMap(map: MutableMap<String, TodoStatus>): String {
        return Json.encodeToString(map)
    }
}