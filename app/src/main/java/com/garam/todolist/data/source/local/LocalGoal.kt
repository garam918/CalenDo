package com.garam.todolist.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.garam.todolist.data.GoalType


@Entity(tableName = "goal")
data class LocalGoal(
    @PrimaryKey val goalId: String,
    val title: String,
    val startDate: String,
    val endDate: String,
    val type: GoalType,
    val userId: String
)
