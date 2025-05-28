package com.garam.todolist.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.garam.todolist.data.CategoryIconType


@Entity(
    tableName = "category"
)
data class LocalCategory(
    @PrimaryKey val categoryId : String,
    var title : String,
    var index : Int,
    var icon : CategoryIconType,
    var color : String,
    var userId : String
)
