package com.garam.todolist.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.garam.todolist.data.CategoryIconType
import com.garam.todolist.data.TodoStatus
import com.google.firebase.Timestamp

@Entity(
    tableName = "todo"
)
data class LocalTodo(
    @PrimaryKey val id : String,
    val categoryId : String?,
    val title : String,
    val startDate : String,
    val endDate : String,
    val repeatRule : String?,
    val status : MutableMap<String,TodoStatus>?,
    val priority : Boolean,
    val memo : String,
    val icon : CategoryIconType?,
    val color : String?,
    val startTime : String?,
    val index : Int?,
    val userId : String,
    val savedTime : Long
)