package com.garam.todolist.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "user"
)
data class LocalUserData(
    @PrimaryKey val uid : String,
    val email: String?,
    val loginType : String
)